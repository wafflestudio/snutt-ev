package com.wafflestudio.snuttev.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.Range
import javax.validation.constraints.NotNull

data class CreateEvaluationRequest(
    @field:NotNull
    val content: String,

    @field:NotNull
    @JsonProperty("taken_year")
    val takenYear: Int,

    @field:NotNull
    @JsonProperty("taken_semester")
    val takenSemester: Int,

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
