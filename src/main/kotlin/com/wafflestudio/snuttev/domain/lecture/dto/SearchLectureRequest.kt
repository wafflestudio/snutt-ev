package com.wafflestudio.snuttev.domain.lecture.dto

data class SearchLectureRequest (
    val query: String? = null,
    val classification: List<String>? = null,
    val credit: List<Int>? = null,
    val grade: List<String>? = null,
    val instructor: List<String>? = null,
    val department: List<String>? = null,
    val category: List<String>? = null,
    val page: Int = 0,
)
