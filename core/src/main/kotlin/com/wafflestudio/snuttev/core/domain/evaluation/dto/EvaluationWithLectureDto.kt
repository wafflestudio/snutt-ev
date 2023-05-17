package com.wafflestudio.snuttev.core.domain.evaluation.dto

import com.querydsl.core.annotations.QueryProjection

data class EvaluationWithLectureDto @QueryProjection constructor(
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
    val lectureTitle: String,
    val lectureInstructor: String,
) {
    fun toResponse(userId: String) = EvaluationWithLectureResponse(
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
        lecture = SimpleLectureDto(
            id = this.lectureId,
            title = this.lectureTitle,
            instructor = this.lectureInstructor,
        ),
        isModifiable = this.userId == userId,
        isReportable = this.userId != userId,
    )
}

data class SimpleLectureDto(
    val id: Long,

    val title: String,

    val instructor: String,
)
