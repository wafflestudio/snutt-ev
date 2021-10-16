package com.wafflestudio.snuttev.dao.model

import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.OneToMany

@Entity
data class Lecture(

    val title: String,

    val instructor: String,

    val department: String,

    val courseNumber: String = "",

    val lectureNumber: String = "",

    @OneToMany
    @JoinColumn(name = "lecture_id")
    var semesterLectures: List<SemesterLecture> = emptyList()

) : BaseEntity()
