package com.wafflestudio.snuttev.core.domain.evaluation.dto

import com.querydsl.core.annotations.QueryProjection

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
    val isHidden: Boolean,
    val isReported: Boolean,
    val isLiked: Boolean,
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
        isLiked = this.isLiked,
        fromSnuev = this.fromSnuev,
        year = this.year,
        semester = this.semester,
        lectureId = this.lectureId,
        isModifiable = this.userId == userId,
        isReportable = this.userId != userId,
    )
}
