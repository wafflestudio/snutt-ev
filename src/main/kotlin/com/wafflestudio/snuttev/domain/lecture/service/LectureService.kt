package com.wafflestudio.snuttev.domain.lecture.service

import com.wafflestudio.snuttev.domain.evaluation.dto.SemesterLectureDto
import com.wafflestudio.snuttev.domain.lecture.dto.GetSemesterLecturesResponse
import com.wafflestudio.snuttev.domain.lecture.dto.SearchLectureRequest
import com.wafflestudio.snuttev.domain.lecture.dto.SearchLectureResponse
import com.wafflestudio.snuttev.domain.lecture.model.SemesterLecture
import com.wafflestudio.snuttev.domain.lecture.repository.LectureRepository
import com.wafflestudio.snuttev.domain.lecture.repository.SemesterLectureRepository
import com.wafflestudio.snuttev.error.LectureNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class LectureService(
    private val lectureRepository: LectureRepository,
    private val semesterLectureRepository: SemesterLectureRepository
) {
    fun search(request: SearchLectureRequest): Page<SearchLectureResponse> {
        val pageable = PageRequest.of(request.page, 20)
        return when {
            (request.year == null && request.semester == null) -> {
                lectureRepository.searchLectures(request, pageable).map { SearchLectureResponse(it) }
            }
            else -> lectureRepository.searchSemesterLectures(request, pageable).map { SearchLectureResponse(it) }
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
