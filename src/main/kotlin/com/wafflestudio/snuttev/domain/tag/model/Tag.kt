package com.wafflestudio.snuttev.domain.tag.model

import com.wafflestudio.snuttev.domain.common.model.BaseEntity
import javax.persistence.*

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["tag_group_id", "ordering"])])
class Tag(

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_group_id")
    val tagGroup: TagGroup,

    @Column(nullable = false, unique = true)
    val name: String,

    @Column(nullable = false)
    val ordering: Int,

    @Column(name = "value_type", nullable = false)
    @Enumerated(EnumType.STRING)
    val valueType: TagValueType,

    @Column(name = "int_value")
    val intValue: Int? = null,

    @Column(name = "string_value")
    val stringValue: String? = null,

) : BaseEntity()

enum class TagValueType {
    INT,
    STRING,
    LOGIC,
}
