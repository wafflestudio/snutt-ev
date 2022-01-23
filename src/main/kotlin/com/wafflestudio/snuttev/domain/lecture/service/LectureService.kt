package com.wafflestudio.snuttev.domain.lecture.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.snuttev.domain.evaluation.dto.SemesterLectureDto
import com.wafflestudio.snuttev.domain.lecture.dto.GetSemesterLecturesResponse
import com.wafflestudio.snuttev.domain.lecture.dto.SearchLectureRequest
import com.wafflestudio.snuttev.domain.lecture.dto.SearchQuery
import com.wafflestudio.snuttev.domain.lecture.dto.SearchLectureResponse
import com.wafflestudio.snuttev.domain.lecture.model.SemesterLecture
import com.wafflestudio.snuttev.domain.lecture.repository.LectureRepository
import com.wafflestudio.snuttev.domain.lecture.repository.SemesterLectureRepository
import com.wafflestudio.snuttev.domain.tag.model.TagValueType
import com.wafflestudio.snuttev.domain.tag.repository.TagRepository
import com.wafflestudio.snuttev.error.LectureNotFoundException
import com.wafflestudio.snuttev.error.WrongSearchTagException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class LectureService(
    private val lectureRepository: LectureRepository,
    private val semesterLectureRepository: SemesterLectureRepository,
    private val tagRepository: TagRepository
) {
    fun search(param: SearchLectureRequest): Page<SearchLectureResponse> {
        val request = mappingTagsToLectureProperty(param)
        val pageable = PageRequest.of(param.page, 20)
        return when {
            (request.year == null && request.semester == null) -> {
                lectureRepository.searchLectures(request, pageable)
            }
            else -> lectureRepository.searchSemesterLectures(request, pageable)
        }
    }

    fun getSemesterLectures(
        lectureId: Long
    ): GetSemesterLecturesResponse {
        val semesterLectures = semesterLectureRepository.findAllByLectureIdOrderByYearDescSemesterDesc(lectureId)
        if (semesterLectures.isEmpty()) {
            throw LectureNotFoundException
        }

        return GetSemesterLecturesResponse(
            semesterLectures.map {
                genSemesterLectureDto(it)
            }
        )
    }

    private fun mappingTagsToLectureProperty(request: SearchLectureRequest): SearchQuery {
        val tags = tagRepository.getTagsWithTagGroupByTagsIdIsIn(request.tags)
        val tagMap: Map<String, List<Any>> = tags.groupBy({ it.tagGroup.name }, {
            when (it.tagGroup.valueType) {
                TagValueType.INT -> it.intValue!!
                TagValueType.STRING -> it.stringValue!!
                TagValueType.LOGIC -> ""
            }
        })
        val (year, semester) = tagMap["semester"]?.filterIsInstance<String>()?.let {
            if (it.size != 1) throw WrongSearchTagException
            val pair = it[0].split(",")
            if (pair.size != 2) throw WrongSearchTagException
            Pair(pair[0].toInt(), pair[1].toInt())
        } ?: Pair(null, null)
        return SearchQuery(
            query = request.query,
            classification = tagMap["classification"]?.filterIsInstance<String>(),
            credit = tagMap["학점"]?.filterIsInstance<Int>(),
            academicYear = tagMap["학년"]?.filterIsInstance<String>(),
            department = tagMap["category"]?.filterIsInstance<String>(),
            year = year,
            semester = semester
        )
    }

    private fun genSemesterLectureDto(semesterLecture: SemesterLecture): SemesterLectureDto =
        SemesterLectureDto(
            id = semesterLecture.id!!,
            lectureNumber = semesterLecture.lectureNumber,
            year = semesterLecture.year,
            semester = semesterLecture.semester,
            credit = semesterLecture.credit,
            extraInfo = semesterLecture.extraInfo,
            academicYear = semesterLecture.academicYear,
            category = semesterLecture.category,
            classification = semesterLecture.classification,
        )

}
