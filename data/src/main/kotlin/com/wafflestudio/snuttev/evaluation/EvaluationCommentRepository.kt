package com.wafflestudio.snuttev.evaluation

import org.springframework.data.jpa.repository.JpaRepository

interface EvaluationCommentRepository : JpaRepository<EvaluationComment, Long> {

}
