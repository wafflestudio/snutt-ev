package com.wafflestudio.snuttev.domain.evaluation.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.wafflestudio.snuttev.domain.common.model.BaseEntity
import com.wafflestudio.snuttev.domain.lecture.model.SemesterLecture
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class LectureEvaluation(

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_lecture_id")
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
    val likeCount: Long = 0,

    @Column(name = "dislike_count", nullable = false)
    val dislikeCount: Long = 0,

    @Column(name = "is_hidden", nullable = false)
    var isHidden: Boolean = false,

    @Column(name = "is_reported", nullable = false)
    val isReported: Boolean = false,

    @Column(name = "from_snuev", nullable = false)
    val fromSnuev: Boolean = false,

    createdAt: LocalDateTime = LocalDateTime.now(),

    ) : BaseEntity(createdAt = createdAt)

data class LectureEvaluationWithLecture(
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

    val fromSnuev: Boolean? = null,

    val year: Int? = null,

    val semester: Int? = null,

    val lectureId: Long? = null,

    val lectureTitle: String? = null,

    val lectureInstructor: String? = null,
)
