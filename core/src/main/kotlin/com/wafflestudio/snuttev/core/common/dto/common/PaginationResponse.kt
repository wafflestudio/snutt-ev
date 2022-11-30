package com.wafflestudio.snuttev.core.common.dto.common

import org.springframework.data.domain.Page

data class PaginationResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val last: Boolean,
    val totalCount: Long? = null
) {
    constructor(page: Page<T>) : this(
        content = page.content,
        page = page.number,
        size = page.size,
        last = page.isLast,
        totalCount = page.totalElements
    )
}

data class CursorPaginationResponse<T> (
    val content: List<T>,
    val cursor: String?,
    val size: Int,
    val last: Boolean,
    val totalCount: Long? = null
)
