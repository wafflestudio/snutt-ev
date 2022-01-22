package com.wafflestudio.snuttev.domain.tag.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class TagGroupDto(
    val id: Long,

    val name: String,

    val ordering: Int,

    val tags: List<TagDto>
)

data class TagDto(
    val id: Long,

    val name: String,

    val ordering: Int,
)

data class SearchTagResponse(
    @JsonProperty("tag_groups")
    val tagGroups: List<TagGroupDto>
)
