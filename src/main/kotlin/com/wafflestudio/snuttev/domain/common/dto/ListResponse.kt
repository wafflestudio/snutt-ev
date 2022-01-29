package com.wafflestudio.snuttev.domain.common.dto

import com.fasterxml.jackson.annotation.JsonProperty


data class ListResponse<T> (
    val content: List<T>,

    @JsonProperty("total_count")
    val totalCount: Int = content.size,
)
