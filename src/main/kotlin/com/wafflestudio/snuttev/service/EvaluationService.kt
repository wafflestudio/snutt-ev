package com.wafflestudio.snuttev.service

import com.wafflestudio.snuttev.dao.model.LectureEvaluation
import com.wafflestudio.snuttev.dao.repository.LectureEvaluationRepository
import com.wafflestudio.snuttev.dao.repository.LectureRepository
import com.wafflestudio.snuttev.dao.repository.SemesterLectureRepository
import com.wafflestudio.snuttev.dto.CreateEvaluationRequest
import com.wafflestudio.snuttev.dto.LectureEvaluationDto
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
    ): LectureEvaluationDto? {
        val semesterLecture = semesterLectureRepository.findByIdOrNull(semesterLectureId)
        semesterLecture?.let {
            val lectureEvaluation = LectureEvaluation(
                semesterLecture = it,
                userId = userId,
                content = createEvaluationRequest.content,
                takenYear = createEvaluationRequest.takenYear,
                takenSemester = createEvaluationRequest.takenSemester,
                gradeSatisfaction = createEvaluationRequest.gradeSatisfaction,
                teachingSkill = createEvaluationRequest.teachingSkill,
                gains = createEvaluationRequest.gains,
                lifeBalance = createEvaluationRequest.lifeBalance,
                rating = createEvaluationRequest.rating,
            )
            lectureEvaluationRepository.save(lectureEvaluation)
            return genLectureEvaluationDto(lectureEvaluation)
        }
        return null
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
            takenYear = lectureEvaluation.takenYear,
            takenSemester = lectureEvaluation.takenSemester,
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
}
