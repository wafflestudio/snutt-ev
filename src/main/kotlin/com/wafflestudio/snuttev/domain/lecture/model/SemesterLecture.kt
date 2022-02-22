package com.wafflestudio.snuttev.domain.lecture.model

import com.wafflestudio.snuttev.domain.common.model.BaseEntity
import com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluation
import javax.persistence.*

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["lecture_id", "year", "semester"])])
class SemesterLecture(

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    var lecture: Lecture,

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
    val evaluations: List<LectureEvaluation> = listOf()
) : BaseEntity()

data class SemesterLectureWithLecture(
    val id: Long? = null,

    val lectureNumber: String,

    val year: Int,

    val semester: Int,

    var credit: Int,

    var extraInfo: String,

    var academicYear: String,

    var category: String,

    var classification: String,

    val lectureId: Long,

    val title: String,

    val instructor: String,

    val department: String,

    val courseNumber: String,
)
