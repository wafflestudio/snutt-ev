package com.wafflestudio.snuttev.service

import com.wafflestudio.snuttev.dao.model.LectureEvaluation
import com.wafflestudio.snuttev.dao.model.SemesterLecture
import com.wafflestudio.snuttev.dao.repository.LectureEvaluationRepository
import com.wafflestudio.snuttev.dao.repository.LectureRepository
import com.wafflestudio.snuttev.dao.repository.SemesterLectureRepository
import com.wafflestudio.snuttev.dto.CreateEvaluationRequest
import com.wafflestudio.snuttev.dto.GetSemesterLecturesResponse
import com.wafflestudio.snuttev.dto.LectureEvaluationDto
import com.wafflestudio.snuttev.dto.SemesterLectureDto
import com.wafflestudio.snuttev.error.LectureNotFoundException
import com.wafflestudio.snuttev.error.SemesterLectureNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class EvaluationService(
    private val semesterLectureRepository: SemesterLectureRepository,
    private val lectureEvaluationRepository: LectureEvaluationRepository,
    private val lectureRepository: LectureRepository
) {
    fun createEvaluation(
        userId: String,
        semesterLectureId: Long,
        createEvaluationRequest: CreateEvaluationRequest
    ): LectureEvaluationDto {
        val semesterLecture = semesterLectureRepository.findByIdOrNull(semesterLectureId) ?: throw SemesterLectureNotFoundException

        val lectureEvaluation = LectureEvaluation(
            semesterLecture = semesterLecture,
            userId = userId,
            content = createEvaluationRequest.content,
            gradeSatisfaction = createEvaluationRequest.gradeSatisfaction,
            teachingSkill = createEvaluationRequest.teachingSkill,
            gains = createEvaluationRequest.gains,
            lifeBalance = createEvaluationRequest.lifeBalance,
            rating = createEvaluationRequest.rating,
        )
        lectureEvaluationRepository.save(lectureEvaluation)
        return genLectureEvaluationDto(lectureEvaluation)
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

    fun getLectureEvaluationsOfLecture(lectureId: Long): List<LectureEvaluationDto> {
        val result = lectureRepository.getById(lectureId).semesterLectures.flatMap { it.lectureEvaluations }
        return result.map { genLectureEvaluationDto(it) }
    }

    private fun genLectureEvaluationDto(lectureEvaluation: LectureEvaluation): LectureEvaluationDto =
        LectureEvaluationDto(
            id = lectureEvaluation.id!!,
            userId = lectureEvaluation.userId,
            content = lectureEvaluation.content,
            gradeSatisfaction = lectureEvaluation.gradeSatisfaction,
            teachingSkill = lectureEvaluation.teachingSkill,
            gains = lectureEvaluation.gains,
            lifeBalance = lectureEvaluation.lifeBalance,
            rating = lectureEvaluation.rating,
            likeCount = lectureEvaluation.likeCount,
            dislikeCount = lectureEvaluation.dislikeCount,
            isHidden = lectureEvaluation.isHidden,
            isReported = lectureEvaluation.isReported,
        )

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
