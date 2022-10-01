package com.wafflestudio.snuttev.core.domain.evaluation.repository

import com.wafflestudio.snuttev.core.domain.evaluation.model.EvaluationComment
import org.springframework.data.jpa.repository.JpaRepository

interface EvaluationCommentRepository : JpaRepository<EvaluationComment, Long>
