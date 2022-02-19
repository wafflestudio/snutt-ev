package com.wafflestudio.snuttev.domain.lecture.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SearchLectureRequest(
    val query: String = "",
    val tags: List<Long> = emptyList(),
    val page: Int = 0
)

data class SnuttLectureInfo(
    val year: Int,
    val semester: Int,
    val instructor: String?,
    @JsonProperty("course_number")
    val courseNumber: String?,
)
