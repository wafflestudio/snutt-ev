package com.wafflestudio.snuttev.core.common.dto

data class SearchQueryDto(
    val query: String? = null,
    val classification: List<String>? = null,
    val credit: List<Int>? = null,
    val academicYear: List<String>? = null,
    val department: List<String>? = null,
    val category: List<String>? = null,
    val semesters: List<Pair<Int,Int>> = listOf(),
)
