package com.wafflestudio.snuttev.core.domain.tag.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class TagGroupDto(
    val id: Long,

    val name: String,

    val ordering: Int,

    val color: String?,

    val tags: List<TagDto>
)

data class TagDto(
    val id: Long,

    val name: String,

    val description: String?,

    val ordering: Int,
)

data class SearchTagResponse(
    @JsonProperty("tag_groups")
    val tagGroups: List<TagGroupDto>
)
