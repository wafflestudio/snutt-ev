package com.wafflestudio.snuttev.domain.lecture.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.wafflestudio.snuttev.domain.evaluation.dto.SemesterLectureDto

data class LectureDto(
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

    val evaluation: LectureEvaluationSimpleSummary
)

data class LectureEvaluationSimpleSummary(
    @JsonProperty("avg_rating")
    val avgRating: Double?,
)

data class LectureAndSemesterLecturesResponse(
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

    @JsonProperty("semester_lectures")
    val semesterLectures: List<SemesterLectureDto>
)

data class LectureIdResponse(
    val id: Long,
)
