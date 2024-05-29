package com.wafflestudio.snuttev.core.domain.lecture.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
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
    val evaluation: LectureEvaluationSimpleSummary,
)

data class LectureEvaluationSimpleSummary(
    val avgRating: Double?,
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
    val semesterLectures: List<SemesterLectureDto>,
)

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy::class)
data class LectureIdResponse(
    // FIXME: evLectureId로 대체되므로 삭제 (evLectureId 생성: 2024-04-14)
    val id: Long,
    val snuttId: String? = null,
    val evLectureId: Long = id,
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
    val takenSemester: Int,
)

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy::class)
data class EvLectureSummaryForSnutt(
    val snuttId: String,
    val evLectureId: Long,
    val avgRating: Double?,
    val evaluationCount: Long?
)
