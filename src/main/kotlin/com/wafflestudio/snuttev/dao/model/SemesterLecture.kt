package com.wafflestudio.snuttev.dao.model

import javax.persistence.*

@Entity
data class SemesterLecture (

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    val lecture: Lecture,

    val year: Int,

    val semester: Int,

    val credit: Int,

    val extraInfo: String = ""

) : BaseEntity()
