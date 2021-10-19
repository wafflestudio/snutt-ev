package com.wafflestudio.snuttev.service

import com.fasterxml.jackson.annotation.JsonProperty
import com.wafflestudio.snuttev.controller.CreateEvaluationRequest
import com.wafflestudio.snuttev.dao.model.LectureEvaluation
import com.wafflestudio.snuttev.dao.repository.LectureEvaluationRepository
import com.wafflestudio.snuttev.dao.repository.LectureRepository
import com.wafflestudio.snuttev.dao.repository.SemesterLectureRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class EvaluationService(
    private val semesterLectureRepository: SemesterLectureRepository,
    private val lectureEvaluationRepository: LectureEvaluationRepository,
    private val lectureRepository: LectureRepository
) {
    fun createEvaluation(userId: String, createEvaluationRequest: CreateEvaluationRequest): LectureEvaluationDto? {
        val semesterLecture = semesterLectureRepository.findByIdOrNull(createEvaluationRequest.semesterLectureId)
        semesterLecture?.let {
            val lectureEvaluation = LectureEvaluation(
                semesterLecture = it,
                userId = userId,
                content = createEvaluationRequest.content
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
            likeCount = lectureEvaluation.likeCount,
            dislikeCount = lectureEvaluation.dislikeCount,
            isHidden = lectureEvaluation.isHidden,
            isReported = lectureEvaluation.isReported
        )
}

data class LectureEvaluationDto(
    val id: Long,
    @JsonProperty("user_id")
    val userId: String,
    @JsonProperty("like_count")
    val likeCount: Long,
    @JsonProperty("dislike_count")
    val dislikeCount: Long,
    @JsonProperty("is_hidden")
    val isHidden: Boolean,
    @JsonProperty("is_reported")
    val isReported: Boolean
)
