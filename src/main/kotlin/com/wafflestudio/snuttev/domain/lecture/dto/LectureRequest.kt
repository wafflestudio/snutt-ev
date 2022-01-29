package com.wafflestudio.snuttev.domain.lecture.dto

data class SearchLectureRequest(
    val query: String = "",
    val tags: List<Long> = emptyList(),
    val page: Int = 0
)

data class SnuttLectureInfo(
    val year: Int,
    val semester: Int,
    val instructor: String,
    val courseNumber: String,
)
