package com.wafflestudio.snuttev.domain.lecture.model

import com.fasterxml.jackson.annotation.JsonValue
import com.wafflestudio.snuttev.domain.common.model.BaseEntity
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

    var classification: String,

    @OneToMany(mappedBy = "lecture")
    val semesterLectures: List<SemesterLecture> = listOf()
) : BaseEntity()


enum class LectureClassification(@get:JsonValue val value: String) {
    LIBERAL_EDUCATION("교양"),
    ELECTIVE_SUBJECT("전선"),
    REQUISITE_SUBJECT("전필"),
    ELECTIVE_GENERAL("일선"),
    READING_AND_RESEARCH("논문"),
    TEACHING_CERTIFICATION("교직"),
    GRADUATE("대학원"),
    CORE_SUBJECT("공통"),
}


data class LectureEvaluationSummaryDao(
    val id: Long?,

    val title: String?,

    val instructor: String?,

    val department: String?,

    val courseNumber: String?,

    val credit: Int?,

    val academicYear: String?,

    val category: String?,

    val classification: String?,

    val avgGradeSatisfaction: Double?,

    val avgTeachingSkill: Double?,

    val avgGains: Double?,

    val avgLifeBalance: Double?,

    val avgRating: Double?,
)
