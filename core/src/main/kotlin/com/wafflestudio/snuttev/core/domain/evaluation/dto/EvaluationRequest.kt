package com.wafflestudio.snuttev.core.domain.evaluation.dto

import org.hibernate.validator.constraints.Range
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class CreateEvaluationRequest(
    @field:NotNull
    val content: String,

    @field:NotNull
    @field:Range(min = 0, max = 10)
    val gradeSatisfaction: Double,

    @field:NotNull
    @field:Range(min = 0, max = 10)
    val teachingSkill: Double,

    @field:NotNull
    @field:Range(min = 0, max = 10)
    val gains: Double,

    @field:NotNull
    @field:Range(min = 0, max = 10)
    val lifeBalance: Double,

    @field:NotNull
    @field:Range(min = 0, max = 10)
    val rating: Double,
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
