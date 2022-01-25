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

    @Enumerated(EnumType.STRING)
    var classification: String,

) : BaseEntity()

enum class LectureClassification(@get:JsonValue val value: String) {
    LIBERAL_EDUCATION("교양")

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
