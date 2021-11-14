package com.wafflestudio.snuttev.dao.model

import javax.persistence.*

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["lecture_id", "lecture_number", "year", "semester"])])
class SemesterLecture(

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "lecture_id")
    var lecture: Lecture,

    @Column(name = "lecture_number")
    val lectureNumber: String,

    val year: Int,

    val semester: Int,

    var credit: Int,

    @Column(name = "extra_info", columnDefinition = "longtext")
    var extraInfo: String = "",

    @Column(name = "academic_year")
    var academicYear: String,

    var category: String,

    var classfication: String,

    @OneToMany
    @JoinColumn(name = "semester_lecture_id")
    var lectureEvaluations: MutableList<LectureEvaluation> = mutableListOf<LectureEvaluation>()

) : BaseEntity()
