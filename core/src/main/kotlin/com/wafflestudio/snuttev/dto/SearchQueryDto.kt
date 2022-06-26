package com.wafflestudio.snuttev.dto

data class SearchQueryDto(
    val query: String? = null,
    val classification: List<String>? = null,
    val credit: List<Int>? = null,
    val academicYear: List<String>? = null,
    val department: List<String>? = null,
    val category: List<String>? = null,
    val year: Int? = null,
    val semester: Int? = null,
)
