package com.wafflestudio.snuttev

import com.wafflestudio.snuttev.dto.tag.SearchTagResponse
import com.wafflestudio.snuttev.dto.tag.TagDto
import com.wafflestudio.snuttev.dto.tag.TagGroupDto
import com.wafflestudio.snuttev.error.TagGroupNotFoundException
import com.wafflestudio.snuttev.tag.Tag
import com.wafflestudio.snuttev.tag.TagGroup
import com.wafflestudio.snuttev.tag.TagGroupRepository
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
