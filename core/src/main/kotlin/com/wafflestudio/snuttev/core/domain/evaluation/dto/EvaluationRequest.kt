package com.wafflestudio.snuttev.core.domain.evaluation.dto

import org.hibernate.validator.constraints.Range
import javax.validation.constraints.NotBlank

data class CreateEvaluationRequest(
    val content: String,

    @field:Range(min = 0, max = 10)
    val gradeSatisfaction: Double,

    @field:Range(min = 0, max = 10)
    val teachingSkill: Double,

    @field:Range(min = 0, max = 10)
    val gains: Double,

    @field:Range(min = 0, max = 10)
    val lifeBalance: Double,

    @field:Range(min = 0, max = 10)
    val rating: Double,
)

data class UpdateEvaluationRequest(
    val content: String?,

    @field:Range(min = 0, max = 10)
    val gradeSatisfaction: Double?,

    @field:Range(min = 0, max = 10)
    val teachingSkill: Double?,

    @field:Range(min = 0, max = 10)
    val gains: Double?,

    @field:Range(min = 0, max = 10)
    val lifeBalance: Double?,

    @field:Range(min = 0, max = 10)
    val rating: Double?,

    val semesterLectureId: Long?,
)

data class CreateEvaluationReportRequest(
    @field:NotBlank
    val content: String,
)

data class EvaluationCursor(
    val year: Int,
    val semester: Int,
    val lectureEvaluationId: Long,
)
