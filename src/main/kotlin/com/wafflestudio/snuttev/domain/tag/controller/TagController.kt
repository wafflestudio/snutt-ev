package com.wafflestudio.snuttev.domain.tag.controller

import com.wafflestudio.snuttev.domain.tag.dto.SearchTagResponse
import com.wafflestudio.snuttev.domain.tag.dto.TagGroupDto
import com.wafflestudio.snuttev.domain.tag.service.TagService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TagController(
    private val tagService: TagService
) {

    @GetMapping("/v1/tags/main")
    fun getMainTags(): TagGroupDto {
        return tagService.getMainTags()
    }

    @GetMapping("v1/tags/search")
    fun getSearchTags(): SearchTagResponse {
        return tagService.getSearchTags()
    }

}
