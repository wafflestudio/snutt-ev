package com.wafflestudio.snuttev.core.common.dto.common

open class ListResponse<T> (
    val content: List<T>,
    val totalCount: Int = content.size,
)
