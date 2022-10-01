package com.wafflestudio.snuttev.core.config

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManager

@Configuration
class QueryFactoryConfig(
    private val em: EntityManager
) {

    @Bean
    fun queryFactory(): JPAQueryFactory? {
        return JPAQueryFactory(em)
    }
}
