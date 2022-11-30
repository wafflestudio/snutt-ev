package com.wafflestudio.snuttev.core.domain.evaluation.dto

import com.wafflestudio.snuttev.core.common.type.LectureClassification

data class LectureEvaluationDto(
    val id: Long,
    val userId: String,
    val content: String,
    val gradeSatisfaction: Double?,
    val teachingSkill: Double?,
    val gains: Double?,
    val lifeBalance: Double?,
    val rating: Double,
    val likeCount: Long,
    val isHidden: Boolean,
    val isReported: Boolean,
    val fromSnuev: Boolean,
)

data class SemesterLectureDto(
    val id: Long,
    val year: Int,
    val semester: Int,
    val credit: Int,
    val extraInfo: String,
    val academicYear: String,
    val category: String,
    val classification: LectureClassification,
)

data class LectureEvaluationSummaryResponse(
    val id: Long,
    val title: String?,
    val instructor: String?,
    val department: String?,
    val courseNumber: String?,
    val credit: Int?,
    val academicYear: String?,
    val category: String?,
    val classification: LectureClassification?,
    val evaluation: LectureEvaluationSummary,
)

data class LectureEvaluationSummary(
    val avgGradeSatisfaction: Double?,
    val avgTeachingSkill: Double?,
    val avgGains: Double?,
    val avgLifeBalance: Double?,
    val avgRating: Double?,
)

data class EvaluationWithSemesterResponse(
    val id: Long,
    val userId: String,
    val content: String,
    val gradeSatisfaction: Double?,
    val teachingSkill: Double?,
    val gains: Double?,
    val lifeBalance: Double?,
    val rating: Double,
    val likeCount: Long,
    val isHidden: Boolean,
    val isReported: Boolean,
    val isLiked: Boolean,
    val fromSnuev: Boolean,
    val year: Int,
    val semester: Int,
    val lectureId: Long,
    val isModifiable: Boolean,
    val isReportable: Boolean,
)

data class EvaluationWithLectureResponse(
    val id: Long,
    val userId: String,
    val content: String,
    val gradeSatisfaction: Double?,
    val teachingSkill: Double?,
    val gains: Double?,
    val lifeBalance: Double?,
    val rating: Double,
    val likeCount: Long,
    val isHidden: Boolean,
    val isReported: Boolean,
    val isLiked: Boolean,
    val fromSnuev: Boolean,
    val year: Int,
    val semester: Int,
    val lecture: SimpleLectureDto,
    val isModifiable: Boolean,
    val isReportable: Boolean,
)

data class EvaluationsResponse(
    val evaluations: List<EvaluationWithSemesterResponse>,
)

data class EvaluationReportDto(
    val id: Long,
    val lectureEvaluationId: Long,
    val userId: String,
    val content: String,
    val isHidden: Boolean,
)
