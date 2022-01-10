package com.wafflestudio.snuttev.domain.lecture.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SearchLectureRequest (
    val query: String? = null,
    val classification: List<String>? = null,
    val credit: List<Int>? = null,
    @JsonProperty("course_number")
    val courseNumber: List<String>? = null,
    @JsonProperty("academic_year")
    val academicYear: List<String>? = null,
    val instructor: List<String>? = null,
    val department: List<String>? = null,
    val category: List<String>? = null,
    val etc: List<String>? = null,
    val page: Int = 0,
)
