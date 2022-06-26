package com.wafflestudio.snuttev.controller

import com.wafflestudio.snuttev.TagService
import com.wafflestudio.snuttev.dto.tag.SearchTagResponse
import com.wafflestudio.snuttev.dto.tag.TagGroupDto
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
