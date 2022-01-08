package com.wafflestudio.snuttev.domain.lecture.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.wafflestudio.snuttev.domain.lecture.model.Lecture

data class SearchLectureResponse(
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
        lecture.classification,
        lecture.department,
        lecture.academicYear,
        lecture.courseNumber,
        lecture.title,
        lecture.credit,
        lecture.instructor,
        lecture.category
    )
}
