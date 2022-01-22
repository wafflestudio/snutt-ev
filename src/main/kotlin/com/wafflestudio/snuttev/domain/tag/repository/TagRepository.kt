package com.wafflestudio.snuttev.domain.tag.repository

import com.wafflestudio.snuttev.domain.tag.model.Tag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TagRepository : JpaRepository<Tag, Long> {
//    @Query("SELECT t FROM Tag t WHERE t.id IN :tagIdList")
    @Query("SELECT t FROM Tag t JOIN FETCH t.tagGroup WHERE t.id IN :tagIdList")
    fun getTagsWithTagGroupByTagsIdIsIn(tagIdList: List<Long>): List<Tag>
}
