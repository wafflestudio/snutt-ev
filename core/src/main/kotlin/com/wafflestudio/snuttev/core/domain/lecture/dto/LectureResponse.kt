package com.wafflestudio.snuttev.core.domain.lecture.dto

import com.wafflestudio.snuttev.core.common.type.LectureClassification
import com.wafflestudio.snuttev.core.domain.evaluation.dto.SemesterLectureDto

data class LectureDto(
    val id: Long,
    val title: String?,
    val instructor: String?,
    val department: String?,
    val courseNumber: String?,
    val credit: Int?,
    val academicYear: String?,
    val category: String?,
    val classification: LectureClassification?,
    val evaluation: LectureEvaluationSimpleSummary
)

data class LectureEvaluationSimpleSummary(
    val avgRating: Double?
)

data class LectureAndSemesterLecturesResponse(
    val id: Long,
    val title: String?,
    val instructor: String?,
    val department: String?,
    val courseNumber: String?,
    val credit: Int?,
    val academicYear: String?,
    val category: String?,
    val classification: LectureClassification?,
    val semesterLectures: List<SemesterLectureDto>
)

data class LectureIdResponse(
    val id: Long
)

data class LectureTakenByUserResponse(
    val id: Long,
    val title: String?,
    val instructor: String?,
    val department: String?,
    val courseNumber: String?,
    val credit: Int?,
    val academicYear: String?,
    val category: String?,
    val classification: LectureClassification?,
    val takenYear: Int,
    val takenSemester: Int
)
