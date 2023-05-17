package com.wafflestudio.snuttev.core.domain.evaluation.dto

import com.wafflestudio.snuttev.core.common.type.LectureClassification
import com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluation

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
    val myEvaluationExists: Boolean,
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
) {
    companion object {
        fun of(dto: EvaluationWithSemesterDto, userId: String) = EvaluationWithSemesterResponse(
            id = dto.id,
            userId = dto.userId,
            content = dto.content,
            gradeSatisfaction = dto.gradeSatisfaction,
            teachingSkill = dto.teachingSkill,
            gains = dto.gains,
            lifeBalance = dto.lifeBalance,
            rating = dto.rating,
            likeCount = dto.likeCount,
            isHidden = dto.isHidden,
            isReported = dto.isReported,
            isLiked = dto.isLiked,
            fromSnuev = dto.fromSnuev,
            year = dto.year,
            semester = dto.semester,
            lectureId = dto.lectureId,
            isModifiable = dto.userId == userId,
            isReportable = dto.userId != userId,
        )

        fun of(evaluation: LectureEvaluation, userId: String, isLiked: Boolean) = EvaluationWithSemesterResponse(
            id = evaluation.id!!,
            userId = evaluation.userId,
            content = evaluation.content,
            gradeSatisfaction = evaluation.gradeSatisfaction,
            teachingSkill = evaluation.teachingSkill,
            gains = evaluation.gains,
            lifeBalance = evaluation.lifeBalance,
            rating = evaluation.rating,
            likeCount = evaluation.likeCount,
            isHidden = evaluation.isHidden,
            isReported = evaluation.isReported,
            isLiked = isLiked,
            fromSnuev = evaluation.fromSnuev,
            year = evaluation.semesterLecture.year,
            semester = evaluation.semesterLecture.semester,
            lectureId = evaluation.semesterLecture.lecture.id!!,
            isModifiable = evaluation.userId == userId,
            isReportable = evaluation.userId != userId,
        )
    }
}

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
