package com.wafflestudio.snuttev.core.domain.lecture.service

import com.wafflestudio.snuttev.core.common.dto.SearchQueryDto
import com.wafflestudio.snuttev.core.common.error.LectureNotFoundException
import com.wafflestudio.snuttev.core.common.util.SemesterUtils
import com.wafflestudio.snuttev.core.domain.evaluation.dto.SemesterLectureDto
import com.wafflestudio.snuttev.core.domain.evaluation.repository.LectureEvaluationRepository
import com.wafflestudio.snuttev.core.domain.lecture.dto.EvLectureSummaryForSnutt
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureAndSemesterLecturesResponse
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureDto
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureIdResponse
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureTakenByUserResponse
import com.wafflestudio.snuttev.core.domain.lecture.dto.SearchLectureRequest
import com.wafflestudio.snuttev.core.domain.lecture.dto.SnuttLectureInfo
import com.wafflestudio.snuttev.core.domain.lecture.model.SemesterLectureWithLecture
import com.wafflestudio.snuttev.core.domain.lecture.repository.LectureRepository
import com.wafflestudio.snuttev.core.domain.lecture.repository.SemesterLectureRepository
import com.wafflestudio.snuttev.core.domain.lecture.repository.SnuttLectureIdMapRepository
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
    private val snuttLectureIdMapRepository: SnuttLectureIdMapRepository,
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

        val firstSemesterLectureWithLecture = semesterLecturesWithLecture.first()
        val visibleSemesterLecturesWithLecture = semesterLecturesWithLecture.let { semesterLectures ->
            val (year, nextSemester) = semesterUtils.getYearAndSemesterOfNextSemester()
            semesterLectures.filterNot { it.year == year && it.semester == nextSemester.value }
        }

        val evaluations = lectureEvaluationRepository.findBySemesterLectureIdInAndUserIdAndIsHiddenFalse(
            visibleSemesterLecturesWithLecture.map { it.id!! },
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
            semesterLectures = visibleSemesterLecturesWithLecture.map { semesterLecture ->
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

    fun getLectureIdFromSnuttId(snuttId: String): LectureIdResponse {
        val snuttLectureIdMap = snuttLectureIdMapRepository.findBySnuttId(snuttId) ?: throw LectureNotFoundException
        return LectureIdResponse(snuttLectureIdMap.semesterLecture.lecture.id!!, snuttLectureIdMap.snuttId)
    }

    fun getLectureIdsFromSnuttIds(snuttIds: List<String>): List<LectureIdResponse> {
        val snuttLectureIdMaps = snuttLectureIdMapRepository.findAllWithSemesterLectureBySnuttIdIn(snuttIds)
        return snuttLectureIdMaps.map { LectureIdResponse(it.semesterLecture.lecture.id!!, it.snuttId) }
    }

    fun getEvLectureSummaryForSnutt(semesterLectureSnuttIds: List<String>): List<EvLectureSummaryForSnutt> {
        val snuttLectureIdMaps = snuttLectureIdMapRepository.findAllWithSemesterLectureBySnuttIdIn(semesterLectureSnuttIds)
        val lectureIds = snuttLectureIdMaps.map { it.semesterLecture.lecture.id!! }
        val evMap = lectureRepository.findAllRatingsByLectureIds(lectureIds).associateBy { it.id }
        return snuttLectureIdMaps.map {
            val evLectureId = it.semesterLecture.lecture.id!!
            EvLectureSummaryForSnutt(
                snuttId = it.snuttId,
                evLectureId = evLectureId,
                avgRating = evMap[evLectureId]?.avgRating,
                evaluationCount = evMap[evLectureId]?.count ?: 0L,
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
