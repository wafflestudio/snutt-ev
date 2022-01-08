package com.wafflestudio.snuttev.dao.model

import java.time.LocalDateTime
import javax.persistence.*

@MappedSuperclass
open class BaseEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long? = null,

    @Column(nullable = false)
    open val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    open val updatedAt: LocalDateTime? = LocalDateTime.now(),

)
