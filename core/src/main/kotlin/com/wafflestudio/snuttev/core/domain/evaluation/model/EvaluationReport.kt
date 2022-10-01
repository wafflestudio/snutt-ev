package com.wafflestudio.snuttev.core.domain.evaluation.model

import com.wafflestudio.snuttev.core.common.model.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

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
