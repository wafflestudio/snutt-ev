package com.wafflestudio.snuttev.domain.tag.model

import com.wafflestudio.snuttev.domain.common.model.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToMany
import javax.persistence.OrderBy

@Entity
class TagGroup(

    @Column(nullable = false, unique = true)
    val name: String,

    @Column(nullable = false, unique = true)
    val ordering: Int,

    @OneToMany(mappedBy = "tagGroup")
    @OrderBy("ordering ASC")
    var tags: MutableList<Tag> = mutableListOf()

) : BaseEntity()
