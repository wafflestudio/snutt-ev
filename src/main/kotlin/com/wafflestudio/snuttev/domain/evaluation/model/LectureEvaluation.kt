package com.wafflestudio.snuttev.domain.evaluation.model

import com.wafflestudio.snuttev.domain.common.model.BaseEntity
import com.wafflestudio.snuttev.domain.lecture.model.SemesterLecture
import javax.persistence.*

@Entity
class LectureEvaluation(

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_lecture_id")
    var semesterLecture: SemesterLecture,

    @Column(nullable = false)
    val userId: String,

    @Column(columnDefinition = "longtext")
    val content: String,

    @Column(nullable = false)
    val gradeSatisfaction: Double = 0.0,

    @Column(nullable = false)
    val teachingSkill: Double = 0.0,

    @Column(nullable = false)
    val gains: Double = 0.0,

    @Column(nullable = false)
    val lifeBalance: Double = 0.0,

    @Column(nullable = false)
    val rating: Double = 0.0,

    @Column(nullable = false)
    val likeCount: Long = 0,

    @Column(nullable = false)
    val dislikeCount: Long = 0,

    @Column(nullable = false)
    val isHidden: Boolean = false,

    @Column(nullable = false)
    val isReported: Boolean = false,

) : BaseEntity()

data class LectureEvaluationWithSemester(
    val id: Long,

    val userId: String,

    val content: String,

    val gradeSatisfaction: Double = 0.0,

    val teachingSkill: Double = 0.0,

    val gains: Double = 0.0,

    val lifeBalance: Double = 0.0,

    val rating: Double = 0.0,

    val likeCount: Long = 0,

    val dislikeCount: Long = 0,

    val isHidden: Boolean = false,

    val isReported: Boolean = false,

    val year: Int,

    val semester: Int,
)
