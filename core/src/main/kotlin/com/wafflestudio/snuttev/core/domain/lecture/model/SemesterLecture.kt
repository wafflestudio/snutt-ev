package com.wafflestudio.snuttev.core.domain.lecture.model

import com.wafflestudio.snuttev.core.common.model.BaseEntity
import com.wafflestudio.snuttev.core.common.type.LectureClassification
import com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluation
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    uniqueConstraints = [UniqueConstraint(columnNames = ["lecture_id", "year", "semester"])],
    indexes = [Index(name = "semester_lecture_snutt_id_index", columnList = "snutt_id")],
)
class SemesterLecture(
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
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
    val evaluations: List<LectureEvaluation> = listOf(),
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
