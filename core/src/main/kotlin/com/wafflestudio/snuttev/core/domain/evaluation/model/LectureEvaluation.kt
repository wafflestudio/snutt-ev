package com.wafflestudio.snuttev.core.domain.evaluation.model

import com.wafflestudio.snuttev.core.common.model.BaseEntity
import com.wafflestudio.snuttev.core.domain.lecture.model.SemesterLecture
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.OptimisticLockType
import org.hibernate.annotations.OptimisticLocking
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
@DynamicUpdate
@OptimisticLocking(type = OptimisticLockType.DIRTY)
class LectureEvaluation(
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_lecture_id", nullable = false)
    var semesterLecture: SemesterLecture,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(columnDefinition = "longtext", nullable = false)
    var content: String,

    @Column(name = "grade_satisfaction", nullable = true)
    var gradeSatisfaction: Double?,

    @Column(name = "teaching_skill", nullable = true)
    var teachingSkill: Double?,

    @Column(nullable = true)
    var gains: Double?,

    @Column(name = "life_balance", nullable = true)
    var lifeBalance: Double?,

    @Column(nullable = false)
    var rating: Double,

    @Column(name = "like_count", nullable = false)
    var likeCount: Long = 0,

    @Column(name = "is_hidden", nullable = false)
    var isHidden: Boolean = false,

    @Column(name = "is_reported", nullable = false)
    val isReported: Boolean = false,

    @Column(name = "from_snuev", nullable = false)
    val fromSnuev: Boolean = false,

    createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "lectureEvaluation")
    val evaluationLikes: List<EvaluationLike> = listOf()
) : BaseEntity(createdAt = createdAt)
