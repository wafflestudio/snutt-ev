package com.wafflestudio.snuttev.core.domain.evaluation.model

import com.wafflestudio.snuttev.core.common.model.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["lecture_evaluation_id", "user_id"])])
class EvaluationReport(
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_evaluation_id", nullable = false)
    val lectureEvaluation: LectureEvaluation,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(columnDefinition = "longtext", nullable = false)
    val content: String,

    @Column(name = "is_hidden", nullable = false)
    val isHidden: Boolean = false
) : BaseEntity()
