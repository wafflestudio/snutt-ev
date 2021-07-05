package com.wafflestudio.snuttev.dao.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class SemesterLecture (

    val title: String,

    val instructor: String,

    val department: String,

    val year: Int,

    val semester: Int,

    val courseNumber: String = "",

    val lectureNumber: String = ""

) : BaseEntity()
