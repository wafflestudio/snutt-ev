package com.wafflestudio.snuttev.core.domain.evaluation.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.querydsl.core.annotations.QueryProjection

// 왜 nullable 하게 짰었을까?
data class EvaluationWithSemesterDto @QueryProjection constructor(
    val id: Long,

    val userId: String,

    val content: String,

    val gradeSatisfaction: Double,

    val teachingSkill: Double,

    val gains: Double,

    val lifeBalance: Double,

    val rating: Double,

    val likeCount: Long,

    @get:JsonProperty("isHidden")
    @field:JsonProperty("isHidden")
    val isHidden: Boolean,

    @get:JsonProperty("isReported")
    @field:JsonProperty("isReported")
    val isReported: Boolean,

    val fromSnuev: Boolean,

    val year: Int,

    val semester: Int,

    val lectureId: Long,
) {
    fun toResponse(userId: String) = EvaluationWithSemesterResponse(
        id = this.id,
        userId = this.userId,
        content = this.content,
        gradeSatisfaction = this.gradeSatisfaction,
        teachingSkill = this.teachingSkill,
        gains = this.gains,
        lifeBalance = this.lifeBalance,
        rating = this.rating,
        likeCount = this.likeCount,
        isHidden = this.isHidden,
        isReported = this.isReported,
        fromSnuev = this.fromSnuev,
        year = this.year,
        semester = this.semester,
        lectureId = this.lectureId,
        isModifiable = this.userId == userId,
        isReportable = this.userId != userId,
    )
}
