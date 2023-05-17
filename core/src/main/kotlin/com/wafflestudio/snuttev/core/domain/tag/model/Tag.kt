package com.wafflestudio.snuttev.core.domain.tag.model

import com.wafflestudio.snuttev.core.common.model.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["tag_group_id", "ordering"])])
class Tag(
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_group_id", nullable = false)
    val tagGroup: TagGroup,

    @Column(nullable = false)
    val name: String,

    val description: String?,

    @Column(nullable = false)
    val ordering: Int,

    @Column(name = "int_value")
    val intValue: Int? = null,

    @Column(name = "string_value")
    val stringValue: String? = null
) : BaseEntity()
