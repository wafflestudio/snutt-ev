package com.wafflestudio.snuttev.domain.evaluation.dto

import com.fasterxml.jackson.annotation.JsonProperty


data class LectureEvaluationDto(
    val id: Long,

    @JsonProperty("user_id")
    val userId: String,

    val content: String,

    @JsonProperty("grade_satisfaction")
    val gradeSatisfaction: Double,

    @JsonProperty("teaching_skill")
    val teachingSkill: Double,

    val gains: Double,

    @JsonProperty("life_balance")
    val lifeBalance: Double,

    val rating: Double,

    @JsonProperty("like_count")
    val likeCount: Long,

    @JsonProperty("dislike_count")
    val dislikeCount: Long,

    @JsonProperty("is_hidden")
    val isHidden: Boolean,

    @JsonProperty("is_reported")
    val isReported: Boolean,
)

data class SemesterLectureDto(
    val id: Long,

    @JsonProperty("lecture_number")
    val lectureNumber: String,

    val year: Int,

    val semester: Int,

    val credit: Int,

    @JsonProperty("extra_info")
    val extraInfo: String,

    @JsonProperty("academic_year")
    val academicYear: String,

    val category: String,

    val classification: String,
)

data class LectureEvaluationSummaryResponse(
    val id: Long,

    val title: String?,

    val instructor: String?,

    val department: String?,

    @JsonProperty("course_number")
    val courseNumber: String?,

    val credit: Int?,

    @JsonProperty("academic_year")
    val academicYear: String?,

    val category: String?,

    val classification: String?,

    val summary: LectureEvaluationSummary,
)

data class LectureEvaluationSummary(
    @JsonProperty("avg_grade_satisfaction")
    val avgGradeSatisfaction: Double?,

    @JsonProperty("avg_teaching_skill")
    val avgTeachingSkill: Double?,

    @JsonProperty("avg_gains")
    val avgGains: Double?,

    @JsonProperty("avg_life_balance")
    val avgLifeBalance: Double?,

    @JsonProperty("avg_rating")
    val avgRating: Double?,
)

data class CursorPaginationResponse(
    val content: List<Any>,

    val cursor: String?,

    val size: Int,

    val last: Boolean,

    @JsonProperty("total_count")
    val totalCount: Long? = null
)

data class LectureEvaluationWithSemesterDto(
    val id: Long,

    val userId: String,

    val content: String,

    val gradeSatisfaction: Double = 0.0,

    val teachingSkill: Double = 0.0,

    val gains: Double = 0.0,

    val lifeBalance: Double = 0.0,

    val rating: Double = 0.0,

    val likeCount: Long = 0,

    val dislikeCount: Long = 0,

    val isHidden: Boolean = false,

    val isReported: Boolean = false,

    val year: Int,

    val semester: Int,
)
