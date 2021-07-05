package com.wafflestudio.snuttev.dao.repository

import com.wafflestudio.snuttev.dao.model.EvaluationComment
import org.springframework.data.jpa.repository.JpaRepository

interface EvaluationCommentRepository : JpaRepository<EvaluationComment, Long> {

}
