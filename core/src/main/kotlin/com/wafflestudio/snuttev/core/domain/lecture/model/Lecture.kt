package com.wafflestudio.snuttev.core.domain.lecture.model

import com.wafflestudio.snuttev.core.common.model.BaseEntity
import com.wafflestudio.snuttev.core.common.type.LectureClassification
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.NotBlank

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
    val semesterLectures: List<SemesterLecture> = listOf(),
) : BaseEntity()

class LectureClassificationConverter : AttributeConverter<LectureClassification, String> {
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

data class LectureRatingDao(
    val id: Long,
    val avgRating: Double?,
    val count: Long,
)
