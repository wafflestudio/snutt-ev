package com.wafflestudio.snuttev.core.domain.evaluation.model

import com.wafflestudio.snuttev.core.common.model.BaseEntity
import com.wafflestudio.snuttev.core.domain.lecture.model.SemesterLecture
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class LectureEvaluation(
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_lecture_id", nullable = false)
    var semesterLecture: SemesterLecture,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(columnDefinition = "longtext", nullable = false)
    val content: String,

    @Column(name = "grade_satisfaction", nullable = true)
    val gradeSatisfaction: Double?,

    @Column(name = "teaching_skill", nullable = true)
    val teachingSkill: Double?,

    @Column(nullable = true)
    val gains: Double?,

    @Column(name = "life_balance", nullable = true)
    val lifeBalance: Double?,

    @Column(nullable = false)
    val rating: Double,

    @Column(name = "like_count", nullable = false)
    var likeCount: Long = 0,

    @Column(name = "is_hidden", nullable = false)
    var isHidden: Boolean = false,

    @Column(name = "is_reported", nullable = false)
    val isReported: Boolean = false,

    @Column(name = "from_snuev", nullable = false)
    val fromSnuev: Boolean = false,

    createdAt: LocalDateTime = LocalDateTime.now(),
) : BaseEntity(createdAt = createdAt)
