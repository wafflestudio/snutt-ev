package com.wafflestudio.snuttev.dao.model

import javax.persistence.Entity

@Entity
data class Lecture (

    val title: String,

    val instructor: String,

    val department: String,

    val courseNumber: String = "",

    val lectureNumber: String = "",

) : BaseEntity()
