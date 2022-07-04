package com.wafflestudio.snuttev.domain.evaluation.repository

import com.wafflestudio.snuttev.domain.evaluation.model.EvaluationReport
import org.springframework.data.jpa.repository.JpaRepository

interface EvaluationReportRepository : JpaRepository<EvaluationReport, Long> {

    fun existsByLectureEvaluationIdAndUserId(lectureEvaluationId: Long, userId: String): Boolean
}
