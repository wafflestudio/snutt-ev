package com.wafflestudio.snuttev.domain.common.dto


data class ListResponse<T> (
    val content: List<T>,
    val size: Int = content.size,
)
