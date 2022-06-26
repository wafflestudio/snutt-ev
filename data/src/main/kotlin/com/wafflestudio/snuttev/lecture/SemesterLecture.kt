package com.wafflestudio.snuttev.lecture

import com.wafflestudio.snuttev.common.BaseEntity
import com.wafflestudio.snuttev.evaluation.LectureEvaluation
import com.wafflestudio.snuttev.type.LectureClassification
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

    @Convert(converter = LectureClassificationConverter::class)
    var classification: LectureClassification,

    @OneToMany(mappedBy = "semesterLecture")
    val evaluations: List<LectureEvaluation> = listOf()
) : BaseEntity()

data class SemesterLectureWithLecture(
    val id: Long? = null,

    val year: Int,

    val semester: Int,

    var credit: Int,

    var extraInfo: String,

    var academicYear: String,

    var category: String,

    var classification: LectureClassification,

    val lectureId: Long,

    val title: String,

    val instructor: String,

    val department: String,

    val courseNumber: String,
)
