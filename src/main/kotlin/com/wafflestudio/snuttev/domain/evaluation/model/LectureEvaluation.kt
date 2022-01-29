package com.wafflestudio.snuttev.domain.evaluation.model

import com.fasterxml.jackson.annotation.JsonProperty
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
    val gradeSatisfaction: Double,

    @Column(nullable = false)
    val teachingSkill: Double,

    @Column(nullable = false)
    val gains: Double,

    @Column(nullable = false)
    val lifeBalance: Double,

    @Column(nullable = false)
    val rating: Double,

    @Column(nullable = false)
    val likeCount: Long = 0,

    @Column(nullable = false)
    val dislikeCount: Long = 0,

    @Column(nullable = false)
    var isHidden: Boolean = false,

    @Column(nullable = false)
    val isReported: Boolean = false,

) : BaseEntity()

data class LectureEvaluationWithSemester(
    val id: Long? = null,

    val userId: String? = null,

    val content: String? = null,

    val gradeSatisfaction: Double? = null,

    val teachingSkill: Double? = null,

    val gains: Double? = null,

    val lifeBalance: Double? = null,

    val rating: Double? = null,

    val likeCount: Long? = null,

    val dislikeCount: Long? = null,

    @get:JsonProperty("isHidden")
    @field:JsonProperty("isHidden")
    val isHidden: Boolean? = null,

    @get:JsonProperty("isReported")
    @field:JsonProperty("isReported")
    val isReported: Boolean? = null,

    val year: Int? = null,

    val semester: Int? = null,

    val lectureId: Long? = null,
)
