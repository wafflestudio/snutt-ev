package com.wafflestudio.snuttev.domain.common.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.domain.Page

data class PaginationResponse<T>(
    val content: List<T>,

    @JsonProperty("next_page")
    val nextPage: Int,

    val size: Int,

    val last: Boolean,

    @JsonProperty("total_count")
    val totalPages: Int,

    @JsonProperty("total_count")
    val totalCount: Long
) {
    constructor(page: Page<T>) : this(
        content = page.content,
        nextPage = page.number + 1,
        size = page.size,
        last = page.isLast,
        totalPages = page.totalPages,
        totalCount = page.totalElements
    )
}

abstract class CursorPaginationResponse (
    open val content: List<Any>,

    open val cursor: String?,

    open val size: Int,

    open val last: Boolean,

    open val totalCount: Long? = null
)
