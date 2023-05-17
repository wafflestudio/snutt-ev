package com.wafflestudio.snuttev.core.domain.tag.model

import com.wafflestudio.snuttev.core.common.model.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy

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
    LOGIC
}
