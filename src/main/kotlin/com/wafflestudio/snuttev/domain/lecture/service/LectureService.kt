package com.wafflestudio.snuttev.domain.lecture.service

import com.wafflestudio.snuttev.domain.evaluation.dto.SemesterLectureDto
import com.wafflestudio.snuttev.domain.lecture.dto.GetSemesterLecturesResponse
import com.wafflestudio.snuttev.domain.lecture.dto.SearchLectureRequest
import com.wafflestudio.snuttev.domain.lecture.model.Lecture
import com.wafflestudio.snuttev.domain.lecture.model.SemesterLecture
import com.wafflestudio.snuttev.domain.lecture.repository.LectureRepository
import com.wafflestudio.snuttev.error.LectureNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class LectureService(private val lectureRepository: LectureRepository) {
    fun search(searchLectureRequest: SearchLectureRequest): Page<Lecture> {
        val pageable = PageRequest.of(searchLectureRequest.page, 20)
        return lectureRepository.searchLectures(searchLectureRequest, pageable)
    }

    fun getSemesterLectures(
        lectureId: Long
    ): GetSemesterLecturesResponse {
        val lecture = lectureRepository.findByIdOrNull(lectureId) ?: throw LectureNotFoundException

        return GetSemesterLecturesResponse(
            lecture.semesterLectures.map {
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
