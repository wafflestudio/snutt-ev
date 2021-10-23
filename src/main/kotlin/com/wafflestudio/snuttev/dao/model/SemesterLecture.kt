package com.wafflestudio.snuttev.dao.model

import javax.persistence.*

@Entity
class SemesterLecture(

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "lecture_id")
    var lecture: Lecture,

    val year: Int,

    val semester: Int,

    val credit: Int,

    @Column(columnDefinition = "longtext")
    val extraInfo: String = "",

    @OneToMany
    @JoinColumn(name = "semester_lecture_id")
    var lectureEvaluations: List<LectureEvaluation> = emptyList()

) : BaseEntity()
