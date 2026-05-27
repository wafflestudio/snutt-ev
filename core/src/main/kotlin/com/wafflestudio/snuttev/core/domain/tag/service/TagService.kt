package com.wafflestudio.snuttev.core.domain.tag.service

import com.wafflestudio.snuttev.core.common.error.TagGroupNotFoundException
import com.wafflestudio.snuttev.core.common.type.Semester
import com.wafflestudio.snuttev.core.common.util.cache.Cache
import com.wafflestudio.snuttev.core.common.util.cache.CacheKey
import com.wafflestudio.snuttev.core.domain.tag.dto.SearchTagResponse
import com.wafflestudio.snuttev.core.domain.tag.dto.TagDto
import com.wafflestudio.snuttev.core.domain.tag.dto.TagGroupDto
import com.wafflestudio.snuttev.core.domain.tag.model.Tag
import com.wafflestudio.snuttev.core.domain.tag.model.TagGroup
import com.wafflestudio.snuttev.core.domain.tag.repository.TagGroupRepository
import com.wafflestudio.snuttev.core.domain.tag.repository.TagRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TagService internal constructor(
    private val tagGroupRepository: TagGroupRepository,
    private val tagRepository: TagRepository,
    private val cache: Cache,
) {
    companion object {
        private val log = LoggerFactory.getLogger(TagService::class.java)
    }

    @Transactional(readOnly = true)
    fun getMainTags(): TagGroupDto =
        cache.withCache(CacheKey.MAIN_TAGS.build()) {
            val tagGroup = tagGroupRepository.findByName(name = "main") ?: throw TagGroupNotFoundException
            genTagGroupDto(tagGroup)
        }!!

    @Transactional(readOnly = true)
    fun getSearchTags(): SearchTagResponse {
        val tagGroupDtos =
            cache.withCache(CacheKey.SEARCH_TAGS.build()) {
                val tagGroups = tagGroupRepository.findAllByNameNot(name = "main")
                tagGroups.map { genTagGroupDto(it) }
            } ?: emptyList()

        return SearchTagResponse(
            tagGroups = tagGroupDtos,
        )
    }

    private fun genTagGroupDto(tagGroup: TagGroup): TagGroupDto =
        TagGroupDto(
            id = tagGroup.id!!,
            name = tagGroup.name,
            ordering = tagGroup.ordering,
            color = tagGroup.color,
            tags = tagGroup.tags.map { genTagDto(it) },
        )

    private fun genTagDto(tag: Tag): TagDto =
        TagDto(
            id = tag.id!!,
            name = tag.name,
            description = tag.description,
            ordering = tag.ordering,
        )

    @Transactional
    fun saveYearSemesterTagIfNotExists(
        year: Int,
        semester: Int,
    ) {
        val stringValue = "$year,$semester"
        if (tagRepository.searchTagByStringValue(stringValue) == null) {
            val tagGroup =
                tagGroupRepository.findByName(name = "학기") ?: run {
                    log.warn("TagGroup not found. name={}", "학기")
                    return
                }

            val semesterName =
                Semester.labelOfOrNull(semester) ?: run {
                    log.warn("Invalid semester value. semester={}", semester)
                    return
                }

            val name =
                year.toString() + " " + semesterName + "학기"
            val ordering = -1 + tagRepository.findMinOrderingByTagGroupId(tagGroup.id!!)
            tagRepository.save(
                Tag(
                    tagGroup = tagGroup,
                    name = name,
                    ordering = ordering,
                    stringValue = stringValue,
                    description = null,
                ),
            )
        }
    }
}
