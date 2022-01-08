package com.wafflestudio.snuttev.dto

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

data class GetSemesterLecturesResponse(
    @JsonProperty("semester_lectures")
    val semesterLectures: List<SemesterLectureDto>
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
