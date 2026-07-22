package com.wafflestudio.snuttev.core.common.model

import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.OptimisticLock
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@MappedSuperclass
open class BaseEntity(
    // Kotlin val(final 필드)은 Hibernate bytecode enhancer가 @MappedSuperclass에 writer 메서드를
    // 생성하지 않아 자식 엔티티의 enhanced 코드가 NoSuchMethodError로 터진다 (HHH-17418 변종).
    // enhancer가 $$_hibernate_write_*를 생성하도록 var로 선언한다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,
    @Column(name = "created_at", nullable = false)
    open var createdAt: LocalDateTime = LocalDateTime.now(),
    @field:UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @OptimisticLock(excluded = true)
    open var updatedAt: LocalDateTime? = LocalDateTime.now(),
)
