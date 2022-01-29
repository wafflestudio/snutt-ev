package com.wafflestudio.snuttev.domain.evaluation.model

import com.wafflestudio.snuttev.domain.common.model.BaseEntity
import javax.persistence.*

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["lecture_evaluation_id", "user_id"])])
class EvaluationReport(

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_evaluation_id")
    val lectureEvaluation: LectureEvaluation,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(columnDefinition = "longtext", nullable = false)
    val content: String,

    @Column(name = "is_hidden", nullable = false)
    val isHidden: Boolean = false,

) : BaseEntity()
