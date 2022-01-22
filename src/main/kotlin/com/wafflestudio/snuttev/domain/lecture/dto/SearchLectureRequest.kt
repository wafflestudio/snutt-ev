package com.wafflestudio.snuttev.domain.lecture.dto

data class SearchLectureRequest (
    val query: String,
    val tags: List<Long>,
    val page: Int = 0
)

data class SearchQuery (
    val query: String? = null,
    val classification: List<String>? = null,
    val credit: List<Int>? = null,
    val academicYear: List<String>? = null,
    val department: List<String>? = null,
    val category: List<String>? = null,
    val year: Int? = null,
    val semester: Int? = null,
)
