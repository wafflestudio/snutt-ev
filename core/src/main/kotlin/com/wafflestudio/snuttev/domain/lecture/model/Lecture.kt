package com.wafflestudio.snuttev.domain.lecture.model

import com.wafflestudio.snuttev.common.model.BaseEntity
import com.wafflestudio.snuttev.common.type.LectureClassification
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

    @Convert(converter = LectureClassificationConverter::class)
    var classification: LectureClassification,

    @OneToMany(mappedBy = "lecture")
    val semesterLectures: List<SemesterLecture> = listOf()
) : BaseEntity()


class LectureClassificationConverter: AttributeConverter<LectureClassification, String> {
    override fun convertToDatabaseColumn(attribute: LectureClassification?): String? = attribute?.value

    override fun convertToEntityAttribute(dbData: String?): LectureClassification? =
        dbData?.let { LectureClassification.customValueOf(it) }
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

    val classification: LectureClassification?,

    val avgGradeSatisfaction: Double?,

    val avgTeachingSkill: Double?,

    val avgGains: Double?,

    val avgLifeBalance: Double?,

    val avgRating: Double?,
)
