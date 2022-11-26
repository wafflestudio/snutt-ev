package com.wafflestudio.snuttev.core.domain.tag.model

import com.wafflestudio.snuttev.core.common.model.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

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
    val stringValue: String? = null,
) : BaseEntity()
