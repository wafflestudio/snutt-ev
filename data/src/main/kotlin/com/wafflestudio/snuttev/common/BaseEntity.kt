package com.wafflestudio.snuttev.common

import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.*

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
