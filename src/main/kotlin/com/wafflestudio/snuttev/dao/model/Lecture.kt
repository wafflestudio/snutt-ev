package com.wafflestudio.snuttev.dao.model

import javax.persistence.*

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["title", "department", "course_number", "instructor"])])
class Lecture(

    val title: String,

    val instructor: String,

    val department: String,

    @Column(name = "course_number")
    val courseNumber: String = "",

    @OneToMany(mappedBy = "lecture", fetch = FetchType.LAZY)
    var semesterLectures: MutableList<SemesterLecture> = mutableListOf()
) : BaseEntity()
