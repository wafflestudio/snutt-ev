package com.wafflestudio.snuttev.domain.lecture.service

import com.wafflestudio.snuttev.common.dto.SearchQueryDto
import com.wafflestudio.snuttev.domain.evaluation.dto.SemesterLectureDto
import com.wafflestudio.snuttev.common.error.LectureNotFoundException
import com.wafflestudio.snuttev.common.error.WrongSearchTagException
import com.wafflestudio.snuttev.domain.lecture.dto.*
import com.wafflestudio.snuttev.domain.lecture.repository.LectureRepository
import com.wafflestudio.snuttev.domain.lecture.repository.SemesterLectureRepository
import com.wafflestudio.snuttev.domain.lecture.model.SemesterLectureWithLecture
import com.wafflestudio.snuttev.domain.tag.repository.TagRepository
import com.wafflestudio.snuttev.domain.tag.model.TagValueType
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class LectureService(
    private val lectureRepository: LectureRepository,
    private val semesterLectureRepository: SemesterLectureRepository,
    private val tagRepository: TagRepository
) {
    fun search(param: SearchLectureRequest): Page<LectureDto> {
        val request = mappingTagsToLectureProperty(param)
        val pageable = PageRequest.of(param.page, 20)
        return when {
            (request.year == null && request.semester == null) -> {
                lectureRepository.searchLectures(request, pageable)
            }
            else -> lectureRepository.searchSemesterLectures(request, pageable)
        }
    }

    fun getSnuttevLecturesWithSnuttLectureInfos(snuttLectureInfos: List<SnuttLectureInfo>): List<LectureTakenByUserResponse> {
        val distinctLectures = snuttLectureInfos
            .filter { !it.courseNumber.isNullOrEmpty() && !it.instructor.isNullOrEmpty() }
            .associateBy { "${it.courseNumber}${it.instructor}" }
        val lectureKeys = distinctLectures.keys
        val snuttevLectures = lectureRepository.findAllByLectureKeys(lectureKeys)
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
        lectureId: Long
    ): LectureAndSemesterLecturesResponse {
        val semesterLecturesWithLecture =
            semesterLectureRepository.findAllByLectureIdOrderByYearDescSemesterDesc(lectureId)
        if (semesterLecturesWithLecture.isEmpty()) {
            throw LectureNotFoundException
        }

        val firstSemesterLectureWithLecture = semesterLecturesWithLecture.first()

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
            semesterLectures = semesterLecturesWithLecture.map {
                genSemesterLectureDto(it)
            },
        )
    }

    fun getLectureIdFromCourseNumber(courseNumber: String, instructor: String): LectureIdResponse {
        val lecture = lectureRepository.findByCourseNumberAndInstructor(courseNumber, instructor)
            ?: throw LectureNotFoundException
        return LectureIdResponse(lecture.id!!)
    }

    private fun mappingTagsToLectureProperty(request: SearchLectureRequest): SearchQueryDto {
        val tags = tagRepository.getTagsWithTagGroupByTagsIdIsIn(request.tags)
        val tagMap: Map<String, List<Any>> = tags.groupBy({ it.tagGroup.name }, {
            when (it.tagGroup.valueType) {
                TagValueType.INT -> it.intValue!!
                TagValueType.STRING -> it.stringValue!!
                TagValueType.LOGIC -> ""
            }
        })
        val (year, semester) = tagMap["학기"]?.filterIsInstance<String>()?.let {
            if (it.size != 1) throw WrongSearchTagException
            val pair = it[0].split(",")
            if (pair.size != 2) throw WrongSearchTagException
            Pair(pair[0].toInt(), pair[1].toInt())
        } ?: Pair(null, null)
        return SearchQueryDto(
            query = request.query,
            classification = tagMap["구분"]?.filterIsInstance<String>(),
            credit = tagMap["학점"]?.filterIsInstance<Int>(),
            academicYear = tagMap["학년"]?.filterIsInstance<String>(),
            department = tagMap["학과"]?.filterIsInstance<String>(),
            category = tagMap["교양분류"]?.filterIsInstance<String>(),
            year = year,
            semester = semester,
        )
    }

    private fun genSemesterLectureDto(semesterLectureWithLecture: SemesterLectureWithLecture): SemesterLectureDto =
        SemesterLectureDto(
            id = semesterLectureWithLecture.id!!,
            year = semesterLectureWithLecture.year,
            semester = semesterLectureWithLecture.semester,
            credit = semesterLectureWithLecture.credit,
            extraInfo = semesterLectureWithLecture.extraInfo,
            academicYear = semesterLectureWithLecture.academicYear,
            category = semesterLectureWithLecture.category,
            classification = semesterLectureWithLecture.classification,
        )

}
