package com.wafflestudio.snuttev.core.domain.evaluation.repository

import com.wafflestudio.snuttev.core.domain.evaluation.model.EvaluationReport
import org.springframework.data.jpa.repository.JpaRepository

interface EvaluationReportRepository : JpaRepository<EvaluationReport, Long> {

    fun existsByLectureEvaluationIdAndUserId(lectureEvaluationId: Long, userId: String): Boolean
}
