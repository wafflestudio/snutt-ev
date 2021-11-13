package com.wafflestudio.snuttev.dao.model

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["course_number", "instructor"])])
class Lecture(

    val title: String,

    val instructor: String,

    val department: String,

    @Column(name = "course_number")
    @NotBlank
    val courseNumber: String,

    var credit: Int,

    @Column(name = "academic_year")
    var academicYear: String,

    var category: String,

    var classfication: String,


    @OneToMany
    @JoinColumn(name = "lecture_id")
    var semesterLectures: MutableList<SemesterLecture> = mutableListOf<SemesterLecture>()

) : BaseEntity()
