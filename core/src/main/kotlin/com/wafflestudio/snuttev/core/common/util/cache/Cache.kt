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
    @Value("\${spring.data.redis.default-ttl}") private val defaultTtl: Duration,
    private val objectMapper: ObjectMapper
) {
    private val log: Logger get() = LoggerFactory.getLogger(Cache::class.java)

    inline fun <reified T : Any> withCache(
        builtCacheKey: CacheKey.BuiltCacheKey,
        postHitProcessor: (T) -> T = { it },
        supplier: () -> T?
    ): T? {
        get<T>(builtCacheKey)?.let { return postHitProcessor(it) }

        val value = supplier()
        set(builtCacheKey, value)
        return value
    }

    inline fun <reified T : Any> get(builtCacheKey: CacheKey.BuiltCacheKey): T? {
        try {
            log.debug("[CACHE GET] {}", builtCacheKey.key)
            val redisValue = redisTemplate.opsForValue().get(builtCacheKey.key)
            redisValue?.let {
                return objectMapper.readValue(it)
            }
        } catch (e: Exception) {
            log.error(e.message, e)
        }
        return null
    }

    fun <T : Any> set(builtCacheKey: CacheKey.BuiltCacheKey, value: T?) {
        value?.let {
            try {
                log.debug("[CACHE SET] {}", builtCacheKey.key)
                val redisValue = objectMapper.writeValueAsString(value)
                redisTemplate.opsForValue().set(builtCacheKey.key, redisValue, builtCacheKey.ttl ?: defaultTtl)
            } catch (e: Exception) {
                log.error(e.message, e)
            }
        }
    }

    fun delete(builtCacheKey: CacheKey.BuiltCacheKey) {
        try {
            log.debug("[CACHE DELETE] {}", builtCacheKey.key)
            redisTemplate.delete(builtCacheKey.key)
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }

    fun deleteAll(cacheKey: CacheKey) {
        val keys = redisTemplate.keys(cacheKey.keyFormat.replace("%s", "*"))
        try {
            log.debug("[CACHE DELETE ALL] {}", keys)
            redisTemplate.delete(keys)
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }
}
