package com.wafflestudio.snuttev.core.common.dto.common

import com.fasterxml.jackson.annotation.JsonProperty

data class ListResponse<T> (
    val content: List<T>,

    @JsonProperty("total_count")
    val totalCount: Int = content.size,
)
