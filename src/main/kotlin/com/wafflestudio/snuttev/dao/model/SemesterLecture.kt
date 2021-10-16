package com.wafflestudio.snuttev.dao.model

import javax.persistence.*

@Entity
data class SemesterLecture(

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    var lecture: Lecture,

    val year: Int,

    val semester: Int,

    val credit: Int,

    val extraInfo: String = "",

    @OneToMany
    @JoinColumn(name = "semester_lecture_id")
    var lectureEvaluations: List<LectureEvaluation> = emptyList()

) : BaseEntity()
