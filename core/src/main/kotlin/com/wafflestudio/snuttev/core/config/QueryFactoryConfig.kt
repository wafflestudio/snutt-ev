package com.wafflestudio.snuttev.core.config

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QueryFactoryConfig(
    private val em: EntityManager,
) {

    @Bean
    fun queryFactory(): JPAQueryFactory? {
        return JPAQueryFactory(em)
    }
}
