package com.wafflestudio.snuttev.core.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import tools.jackson.databind.ObjectMapper
import java.time.Duration

@Configuration
@EnableCaching
class RedisConfig(
    @param:Value("\${spring.data.redis.default-ttl}") private val redisTtl: Duration,
) {
    @Bean
    fun cacheManager(
        connectionFactory: RedisConnectionFactory,
        objectMapper: ObjectMapper,
    ): CacheManager {
        val redisCacheConfiguration =
            RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(redisTtl)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(
                        GenericJacksonJsonRedisSerializer(objectMapper),
                    ),
                )
        return RedisCacheManager.RedisCacheManagerBuilder
            .fromConnectionFactory(connectionFactory)
            .cacheDefaults(redisCacheConfiguration)
            .build()
    }
}
