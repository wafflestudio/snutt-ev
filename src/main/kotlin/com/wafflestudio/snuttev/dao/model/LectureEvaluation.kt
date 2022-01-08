package com.wafflestudio.snuttev.dao.model

import javax.persistence.*

@Entity
class LectureEvaluation(

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_lecture_id")
    var semesterLecture: SemesterLecture,

    val userId: String,

    @Column(columnDefinition = "longtext")
    val content: String,

    val likeCount: Long = 0,

    val dislikeCount: Long = 0,

    val isHidden: Boolean = false,

    val isReported: Boolean = false

) : BaseEntity()
