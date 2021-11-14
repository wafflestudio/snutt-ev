package com.wafflestudio.snuttev.dao.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class LectureEvaluation(

    @ManyToOne
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
