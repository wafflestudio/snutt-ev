package com.wafflestudio.snuttev.core.domain.tag.dto

data class TagGroupDto(
    val id: Long,
    val name: String,
    val ordering: Int,
    val color: String?,
    val tags: List<TagDto>,
)

data class TagDto(
    val id: Long,
    val name: String,
    val description: String?,
    val ordering: Int,
)

data class SearchTagResponse(
    val tagGroups: List<TagGroupDto>,
)
