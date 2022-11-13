package com.wafflestudio.snuttev.core.domain.evaluation.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.Range
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class CreateEvaluationRequest(
    @field:NotNull
    val content: String,

    @field:NotNull
    @field:Range(min = 0, max = 10)
    @JsonProperty("grade_satisfaction")
    val gradeSatisfaction: Double,

    @field:NotNull
    @field:Range(min = 0, max = 10)
    @JsonProperty("teaching_skill")
    val teachingSkill: Double,

    @field:NotNull
    @field:Range(min = 0, max = 10)
    val gains: Double,

    @field:NotNull
    @field:Range(min = 0, max = 10)
    @JsonProperty("life_balance")
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
) : Comparable<EvaluationCursor> {
    override fun compareTo(other: EvaluationCursor): Int {
        if (year != other.year) {
            return year.compareTo(other.year)
        }
        if (semester != other.semester) {
            return semester.compareTo(other.semester)
        }
        return lectureEvaluationId.compareTo(other.lectureEvaluationId)
    }
}
