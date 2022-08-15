package com.wafflestudio.snuttev.core.common.model

import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
open class BaseEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long? = null,

    @Column(name = "created_at", nullable = false)
    open val createdAt: LocalDateTime = LocalDateTime.now(),

    @field:UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    open val updatedAt: LocalDateTime? = LocalDateTime.now(),

)
