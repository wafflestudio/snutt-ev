package com.wafflestudio.snuttev.core.common.util.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
internal final class Cache(
    private val redisTemplate: StringRedisTemplate,
    @Value("\${spring.redis.ttl}") private val defaultTtl: Duration,
    private val objectMapper: ObjectMapper,
) {
    private val log: Logger get() = LoggerFactory.getLogger(Cache::class.java)

    inline fun <reified T : Any> withCache(cacheKey: CacheKey, vararg args: Any?, supplier: () -> T?): T? {
        val key = cacheKey.key.format(*args)
        try {
            log.debug("[CACHE GET] {}", key)
            val redisValue = redisTemplate.opsForValue().get(key)
            redisValue?.let {
                return objectMapper.readValue(it)
            }
        } catch (e: Exception) {
            log.error(e.message, e)
        }

        val value = supplier()

        set(key, value, cacheKey.ttl ?: defaultTtl)

        return value
    }

    fun <T : Any> set(key: String, value: T?, ttl: Duration) {
        value?.let {
            try {
                log.debug("[CACHE SET] {}", key)
                val redisValue = objectMapper.writeValueAsString(value)
                redisTemplate.opsForValue().set(key, redisValue, ttl)
            } catch (e: Exception) {
                log.error(e.message, e)
            }
        }
    }

    fun delete(cacheKey: CacheKey, vararg args: Any) {
        val key = cacheKey.key.format(*args)
        try {
            log.debug("[CACHE DELETE] {}", key)
            redisTemplate.delete(key)
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }

    fun deleteAll(cacheKey: CacheKey) {
        val keys = redisTemplate.keys(cacheKey.key.replace("%s", "*"))
        try {
            log.debug("[CACHE DELETE ALL] {}", keys)
            redisTemplate.delete(keys)
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }
}
