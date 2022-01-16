package com.wafflestudio.snuttev.domain.tag.repository

import com.wafflestudio.snuttev.domain.tag.model.Tag
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<Tag, Long> {
}
