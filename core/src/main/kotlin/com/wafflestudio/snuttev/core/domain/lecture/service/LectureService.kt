package com.wafflestudio.snuttev.core.domain.lecture.service

import com.wafflestudio.snuttev.core.common.dto.SearchQueryDto
import com.wafflestudio.snuttev.core.common.error.LectureNotFoundException
import com.wafflestudio.snuttev.core.common.util.SemesterUtils
import com.wafflestudio.snuttev.core.domain.evaluation.dto.SemesterLectureDto
import com.wafflestudio.snuttev.core.domain.evaluation.repository.LectureEvaluationRepository
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureAndSemesterLecturesResponse
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureDto
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureIdResponse
import com.wafflestudio.snuttev.core.domain.lecture.dto.EvLectureSummaryForSnutt
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureTakenByUserResponse
import com.wafflestudio.snuttev.core.domain.lecture.dto.SearchLectureRequest
import com.wafflestudio.snuttev.core.domain.lecture.dto.SnuttLectureInfo
import com.wafflestudio.snuttev.core.domain.lecture.model.SemesterLectureWithLecture
import com.wafflestudio.snuttev.core.domain.lecture.repository.LectureRepository
import com.wafflestudio.snuttev.core.domain.lecture.repository.SemesterLectureRepository
import com.wafflestudio.snuttev.core.domain.tag.model.TagValueType
import com.wafflestudio.snuttev.core.domain.tag.repository.TagRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class LectureService(
    private val lectureRepository: LectureRepository,
    private val semesterLectureRepository: SemesterLectureRepository,
    private val tagRepository: TagRepository,
    private val lectureEvaluationRepository: LectureEvaluationRepository,
    private val semesterUtils: SemesterUtils,
) {
    fun search(param: SearchLectureRequest): Page<LectureDto> {
        val request = mappingTagsToLectureProperty(param)
        val pageable = PageRequest.of(param.page, 20)
        return when {
            request.yearSemesters.isEmpty() -> {
                lectureRepository.searchLectures(request, pageable)
            }
            else -> lectureRepository.searchSemesterLectures(request, pageable)
        }
    }

    fun getSnuttevLecturesWithSnuttLectureInfos(
        userId: String,
        snuttLectureInfos: List<SnuttLectureInfo>,
        excludeLecturesWithEvaluations: Boolean,
    ): List<LectureTakenByUserResponse> {
        val distinctLectures = snuttLectureInfos
            .filter { !it.courseNumber.isNullOrEmpty() && !it.instructor.isNullOrEmpty() }
            .associateBy { "${it.courseNumber}${it.instructor}" }
        val lectureKeys = distinctLectures.keys
        var snuttevLectures = lectureRepository.findAllByLectureKeys(lectureKeys)

        if (excludeLecturesWithEvaluations) {
            val lectureIdsWithEvaluation = lectureEvaluationRepository.findLectureIdsByLectureEvaluationUserId(userId)
            snuttevLectures = snuttevLectures.filterNot { lectureIdsWithEvaluation.contains(it.id!!) }
        }

        return snuttevLectures.filter { distinctLectures["${it.courseNumber}${it.instructor}"] != null }.map {
            val snuttInfo = distinctLectures["${it.courseNumber}${it.instructor}"]!!
            LectureTakenByUserResponse(
                id = it.id!!,
                title = it.title,
                instructor = it.instructor,
                department = it.department,
                courseNumber = it.courseNumber,
                credit = it.credit,
                academicYear = it.academicYear,
                category = it.category,
                classification = it.classification,
                takenYear = snuttInfo.year,
                takenSemester = snuttInfo.semester,
            )
        }
    }

    fun getSemesterLectures(
        lectureId: Long,
        userId: String,
    ): LectureAndSemesterLecturesResponse {
        val semesterLecturesWithLecture =
            semesterLectureRepository.findAllByLectureIdOrderByYearDescSemesterDesc(lectureId)
                .ifEmpty { throw LectureNotFoundException }
                .let { semesterLectures ->
                    val (year, nextSemester) = semesterUtils.getYearAndSemesterOfNextSemester()
                    semesterLectures.dropWhile { it.year == year && it.semester == nextSemester.value }
                }

        val firstSemesterLectureWithLecture = semesterLecturesWithLecture.first()

        val semesterLectureIds = semesterLecturesWithLecture.map { it.id!! }
        val evaluations = lectureEvaluationRepository.findBySemesterLectureIdInAndUserIdAndIsHiddenFalse(
            semesterLectureIds,
            userId,
        )

        return LectureAndSemesterLecturesResponse(
            id = firstSemesterLectureWithLecture.lectureId,
            title = firstSemesterLectureWithLecture.title,
            instructor = firstSemesterLectureWithLecture.instructor,
            department = firstSemesterLectureWithLecture.department,
            courseNumber = firstSemesterLectureWithLecture.courseNumber,
            credit = firstSemesterLectureWithLecture.credit,
            academicYear = firstSemesterLectureWithLecture.academicYear,
            category = firstSemesterLectureWithLecture.category,
            classification = firstSemesterLectureWithLecture.classification,
            semesterLectures = semesterLecturesWithLecture.map { semesterLecture ->
                genSemesterLectureDto(
                    semesterLecture,
                    evaluations.any { it.semesterLecture.id == semesterLecture.id },
                )
            },
        )
    }

    fun getLectureIdFromCourseNumber(courseNumber: String, instructor: String): LectureIdResponse {
        val lecture = lectureRepository.findByCourseNumberAndInstructor(courseNumber, instructor)
            ?: throw LectureNotFoundException
        return LectureIdResponse(lecture.id!!)
    }

    fun getEvLectureSummaryForSnutt(semesterLectureSnuttIds: List<String>): List<EvLectureSummaryForSnutt> {
        val lectures = semesterLectureRepository.findAllBySnuttIds(semesterLectureSnuttIds)
        val ratingMap = lectureRepository.findAllRatingsByLectureIds(lectures.map { it.lecture.id!! })
            .associate { it.id to it.avgRating }
        return lectures.map {
            EvLectureSummaryForSnutt(
                snuttId = it.snuttId!!,
                evLectureId = it.lecture.id!!,
                avgRating = ratingMap[it.lecture.id!!],
            )
        }
    }

    private fun mappingTagsToLectureProperty(request: SearchLectureRequest): SearchQueryDto {
        val tags = tagRepository.getTagsWithTagGroupByTagsIdIsIn(request.tags)
        val tagMap: Map<String, List<Any>> = tags.groupBy({ it.tagGroup.name }, {
            when (it.tagGroup.valueType) {
                TagValueType.INT -> it.intValue!!
                TagValueType.STRING -> it.stringValue!!
                TagValueType.LOGIC -> ""
            }
        },)
        val yearSemesters = tagMap["학기"]?.filterIsInstance<String>()?.map {
            val (year, semester) = it.split(",")
            year.toInt() to semester.toInt()
        } ?: listOf()
        return SearchQueryDto(
            query = request.query,
            classification = tagMap["구분"]?.filterIsInstance<String>(),
            credit = tagMap["학점"]?.filterIsInstance<Int>(),
            academicYear = tagMap["학년"]?.filterIsInstance<String>(),
            department = tagMap["학과"]?.filterIsInstance<String>(),
            category = tagMap["교양분류"]?.filterIsInstance<String>(),
            yearSemesters = yearSemesters,
        )
    }

    private fun genSemesterLectureDto(
        semesterLectureWithLecture: SemesterLectureWithLecture,
        myEvaluationExists: Boolean,
    ): SemesterLectureDto =
        SemesterLectureDto(
            id = semesterLectureWithLecture.id!!,
            year = semesterLectureWithLecture.year,
            semester = semesterLectureWithLecture.semester,
            credit = semesterLectureWithLecture.credit,
            extraInfo = semesterLectureWithLecture.extraInfo,
            academicYear = semesterLectureWithLecture.academicYear,
            category = semesterLectureWithLecture.category,
            classification = semesterLectureWithLecture.classification,
            myEvaluationExists = myEvaluationExists,
        )
}
