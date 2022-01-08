package com.wafflestudio.snuttev.domain.evaluation.model

import com.wafflestudio.snuttev.domain.common.model.BaseEntity
import javax.persistence.*

@Entity
class EvaluationComment(

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_id")
    val evaluation: LectureEvaluation,

    @Column(nullable = false)
    val userId: String,

    @Column(columnDefinition = "longtext")
    val content: String,

    @Column(nullable = false)
    val likeCount: Long = 0,

    @Column(nullable = false)
    val dislikeCount: Long = 0,

    @Column(nullable = false)
    val isHidden: Boolean = false,

    @Column(nullable = false)
    val isReported: Boolean = false,

    ) : BaseEntity()
