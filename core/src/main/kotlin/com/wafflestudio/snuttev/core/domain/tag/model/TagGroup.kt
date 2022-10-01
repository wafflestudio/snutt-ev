package com.wafflestudio.snuttev.core.domain.tag.model

import com.wafflestudio.snuttev.core.common.model.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.OneToMany
import javax.persistence.OrderBy

@Entity
class TagGroup(

    @Column(nullable = false, unique = true)
    val name: String,

    @Column(nullable = false, unique = true)
    val ordering: Int,

    val color: String?,

    @Column(name = "value_type", nullable = false)
    @Enumerated(EnumType.STRING)
    val valueType: TagValueType,

    @OneToMany(mappedBy = "tagGroup")
    @OrderBy("ordering ASC")
    var tags: MutableList<Tag> = mutableListOf()

) : BaseEntity()

enum class TagValueType {
    INT,
    STRING,
    LOGIC,
}
