package com.wafflestudio.snuttev.domain.evaluation.repository

import com.wafflestudio.snuttev.domain.evaluation.model.EvaluationComment
import org.springframework.data.jpa.repository.JpaRepository

interface EvaluationCommentRepository : JpaRepository<EvaluationComment, Long> {

}
