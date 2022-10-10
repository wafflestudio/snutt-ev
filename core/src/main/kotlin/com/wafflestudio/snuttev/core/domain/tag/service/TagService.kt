package com.wafflestudio.snuttev.core.domain.tag.service

import com.wafflestudio.snuttev.core.common.error.TagGroupNotFoundException
import com.wafflestudio.snuttev.core.domain.tag.dto.SearchTagResponse
import com.wafflestudio.snuttev.core.domain.tag.dto.TagDto
import com.wafflestudio.snuttev.core.domain.tag.dto.TagGroupDto
import com.wafflestudio.snuttev.core.domain.tag.model.Tag
import com.wafflestudio.snuttev.core.domain.tag.model.TagGroup
import com.wafflestudio.snuttev.core.domain.tag.repository.TagGroupRepository
import org.springframework.stereotype.Service

@Service
class TagService(
    private val tagGroupRepository: TagGroupRepository,
) {
    fun getMainTags(): TagGroupDto {
        val tagGroup = tagGroupRepository.findByName(name = "main") ?: throw TagGroupNotFoundException
        return genTagGroupDto(tagGroup)
    }

    fun getSearchTags(): SearchTagResponse {
        val tagGroups = tagGroupRepository.findAllByNameNotOrderByOrdering(name = "main")
        return SearchTagResponse(
            tagGroups = tagGroups.map { genTagGroupDto(it) }
        )
    }

    private fun genTagGroupDto(tagGroup: TagGroup): TagGroupDto {
        return TagGroupDto(
            id = tagGroup.id!!,
            name = tagGroup.name,
            ordering = tagGroup.ordering,
            color = tagGroup.color,
            tags = tagGroup.tags.map { genTagDto(it) },
        )
    }

    private fun genTagDto(tag: Tag): TagDto =
        TagDto(
            id = tag.id!!,
            name = tag.name,
            description = tag.description,
            ordering = tag.ordering,
        )
}
