package com.wafflestudio.snuttev.core.domain.lecture.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.wafflestudio.snuttev.core.domain.evaluation.dto.SemesterLectureDto
import com.wafflestudio.snuttev.core.common.type.LectureClassification

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

    val classification: LectureClassification?,

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

    val classification: LectureClassification?,

    @JsonProperty("semester_lectures")
    val semesterLectures: List<SemesterLectureDto>
)

data class LectureIdResponse(
    val id: Long,
)

data class LectureTakenByUserResponse(
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

    val classification: LectureClassification?,

    @JsonProperty("taken_year")
    val takenYear: Int,

    @JsonProperty("taken_semester")
    val takenSemester: Int
)
