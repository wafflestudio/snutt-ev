package com.wafflestudio.snuttev.evaluation;

import org.springframework.data.jpa.repository.JpaRepository

interface EvaluationReportRepository : JpaRepository<EvaluationReport, Long> {

    fun existsByLectureEvaluationIdAndUserId(lectureEvaluationId: Long, userId: String): Boolean
}
