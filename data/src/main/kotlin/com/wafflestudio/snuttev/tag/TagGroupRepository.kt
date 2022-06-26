package com.wafflestudio.snuttev.tag

import org.springframework.data.jpa.repository.JpaRepository

interface TagGroupRepository : JpaRepository<TagGroup, Long> {
    fun findByName(name: String): TagGroup?

    fun findAllByNameNotOrderByOrdering(name: String): List<TagGroup>
}
