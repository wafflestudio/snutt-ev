package com.wafflestudio.snuttev.dao.model

import javax.persistence.*

@Entity
data class LectureEvaluation (

    @ManyToOne
    @JoinColumn(name = "semester_lecture_id")
    val semesterLecture: SemesterLecture,

    val userId: String,

    @Column(columnDefinition = "longtext")
    val content: String,

    val likeCount: Long = 0,

    val dislikeCount: Long = 0,

    val isHidden: Boolean = false,

    val isReported: Boolean = false

) : BaseEntity()
