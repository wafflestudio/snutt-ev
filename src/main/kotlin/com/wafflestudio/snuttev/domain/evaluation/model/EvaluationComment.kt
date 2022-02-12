package com.wafflestudio.snuttev.domain.evaluation.model

import com.wafflestudio.snuttev.domain.common.model.BaseEntity
import javax.persistence.*

@Entity
class EvaluationComment(

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_evaluation_id")
    val lectureEvaluation: LectureEvaluation,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(columnDefinition = "longtext")
    val content: String,

    @Column(name = "like_count", nullable = false)
    val likeCount: Long = 0,

    @Column(name = "dislike_count", nullable = false)
    val dislikeCount: Long = 0,

    @Column(name = "is_hidden", nullable = false)
    val isHidden: Boolean = false,

    @Column(name = "is_reported", nullable = false)
    val isReported: Boolean = false,

) : BaseEntity()
