package com.wafflestudio.snuttev.core.domain.evaluation.dto

import com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluation
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Range

data class CreateEvaluationRequest(
    val content: String,

    @field:Range(min = 1, max = 5)
    val gradeSatisfaction: Double,

    @field:Range(min = 1, max = 5)
    val teachingSkill: Double,

    @field:Range(min = 1, max = 5)
    val gains: Double,

    @field:Range(min = 1, max = 5)
    val lifeBalance: Double,

    @field:Range(min = 1, max = 5)
    val rating: Double,
)

data class UpdateEvaluationRequest(
    val content: String?,

    @field:Range(min = 1, max = 5)
    val gradeSatisfaction: Double?,

    @field:Range(min = 1, max = 5)
    val teachingSkill: Double?,

    @field:Range(min = 1, max = 5)
    val gains: Double?,

    @field:Range(min = 1, max = 5)
    val lifeBalance: Double?,

    @field:Range(min = 1, max = 5)
    val rating: Double?,

    val semesterLectureId: Long?,
) {
    fun isUpdatingAny(evaluation: LectureEvaluation): Boolean {
        return (content != null && content != evaluation.content) ||
            (gradeSatisfaction != null && gradeSatisfaction != evaluation.gradeSatisfaction) ||
            (teachingSkill != null && teachingSkill != evaluation.teachingSkill) ||
            (gains != null && gains != evaluation.gains) ||
            (lifeBalance != null && lifeBalance != evaluation.lifeBalance) ||
            (rating != null && rating != evaluation.rating) ||
            (semesterLectureId != null && semesterLectureId != evaluation.semesterLecture.id)
    }
}

data class CreateEvaluationReportRequest(
    @field:NotBlank
    val content: String,
)

data class EvaluationCursor(
    val year: Int,
    val semester: Int,
    val lectureEvaluationId: Long,
)
