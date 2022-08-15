package com.wafflestudio.snuttev.core.domain.tag.repository

import com.wafflestudio.snuttev.core.domain.tag.model.TagGroup
import org.springframework.data.jpa.repository.JpaRepository

interface TagGroupRepository : JpaRepository<TagGroup, Long> {
    fun findByName(name: String): TagGroup?

    fun findAllByNameNotOrderByOrdering(name: String): List<TagGroup>
}
