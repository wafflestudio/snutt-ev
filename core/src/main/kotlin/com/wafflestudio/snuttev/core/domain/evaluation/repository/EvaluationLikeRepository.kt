package com.wafflestudio.snuttev.core.domain.evaluation.repository

import com.wafflestudio.snuttev.core.domain.evaluation.model.EvaluationLike
import com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluation
import org.springframework.data.jpa.repository.JpaRepository

interface EvaluationLikeRepository : JpaRepository<EvaluationLike, Long> {
    fun existsByLectureEvaluationAndUserId(lectureEvaluation: LectureEvaluation, userId: String): Boolean

    fun findAllByLectureEvaluationIdIn(lectureEvaluationIds: List<Long>): List<EvaluationLike>

    fun deleteByLectureEvaluationAndUserId(lectureEvaluation: LectureEvaluation, userId: String): Long
}
