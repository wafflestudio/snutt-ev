package com.wafflestudio.snuttev.domain.lecture.model

import com.wafflestudio.snuttev.domain.common.model.BaseEntity
import com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluation
import javax.persistence.*

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["lecture_id", "lecture_number", "year", "semester"])])
class SemesterLecture(

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
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

    var classification: String,

    @OneToMany(mappedBy = "semesterLecture")
    var lectureEvaluations: MutableList<LectureEvaluation> = mutableListOf()

) : BaseEntity()
