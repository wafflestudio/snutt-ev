package com.wafflestudio.snuttev.dao.model

import java.time.LocalDateTime
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
open class BaseEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long? = null,

    open val createdAt: LocalDateTime = LocalDateTime.now(),

    open val updatedAt: LocalDateTime? = LocalDateTime.now()

)
