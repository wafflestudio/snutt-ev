package com.wafflestudio.snuttev.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import java.time.Duration

@Configuration
class RedisConfig(
    @Value("\${spring.redis.ttl}") private val redisTtl: Duration
) {

    @Bean
    fun cacheManager(connectionFactory: RedisConnectionFactory): CacheManager {
        val redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig().entryTtl(redisTtl)
        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(connectionFactory)
                .cacheDefaults(redisCacheConfiguration).build()
    }
}
