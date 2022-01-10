package com.wafflestudio.snuttev.domain.lecture.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.querydsl.core.annotations.QueryProjection
import com.wafflestudio.snuttev.domain.lecture.model.Lecture
import com.wafflestudio.snuttev.domain.lecture.model.SemesterLecture

data class SearchLectureResponse @QueryProjection constructor(
    val id: Long?,
    @JsonProperty("classification") val classification: String?,
    @JsonProperty("department") val department: String?,
    @JsonProperty("academic_year") val academicYear: String?,
    @JsonProperty("course_number") val courseNumber: String?,
    @JsonProperty("title") val title: String,
    @JsonProperty("credit") val credit: Int,
    @JsonProperty("instructor") val instructor: String,
    @JsonProperty("category") val category: String?,
){
    constructor(lecture: Lecture): this(
        lecture.id,
        lecture.classification,
        lecture.department,
        lecture.academicYear,
        lecture.courseNumber,
        lecture.title,
        lecture.credit,
        lecture.instructor,
        lecture.category
    )
    constructor(semesterLecture: SemesterLecture): this(
        semesterLecture.lecture.id,
        semesterLecture.classification,
        semesterLecture.lecture.department,
        semesterLecture.academicYear,
        semesterLecture.lecture.courseNumber,
        semesterLecture.lecture.title,
        semesterLecture.credit,
        semesterLecture.lecture.instructor,
        semesterLecture.category
    )
}
