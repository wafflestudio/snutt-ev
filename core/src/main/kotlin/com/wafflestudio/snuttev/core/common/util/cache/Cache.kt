package com.wafflestudio.snuttev.core.common.util.cache

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import javax.annotation.PostConstruct

@Component
class Cache(
    private val redisTemplate: StringRedisTemplate,
) {
    @PostConstruct
    private fun init() {
        Companion.redisTemplate = redisTemplate
    }

    companion object {
        lateinit var redisTemplate: StringRedisTemplate
        val objectMapper = jacksonObjectMapper()

        val log: Logger get() = LoggerFactory.getLogger(Cache::class.java)

        inline fun <reified T : Any> get(cacheKey: CacheKey, supplier: () -> T?, vararg args: Any?): T? {
            val key = cacheKey.key.format(*args)
            try {
                log.info("[CACHE GET] {}", key)
                val redisValue = redisTemplate.opsForValue().get(key)
                redisValue?.let {
                    return objectMapper.readValue(it)
                }
            } catch (e: Exception) {
                log.error(e.message, e)
            }

            val value = supplier()

            set(key, value, cacheKey.ttl)

            return value
        }

        fun <T : Any> set(key: String, value: T?, ttl: Duration) {
            value?.let {
                try {
                    log.info("[CACHE SET] {}", key)
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
                log.info("[CACHE DELETE] {}", key)
                redisTemplate.delete(key)
            } catch (e: Exception) {
                log.error(e.message, e)
            }
        }

        fun deleteAll(cacheKey: CacheKey) {
            val keys = redisTemplate.keys(cacheKey.key.replace("%s", "*"))
            try {
                log.info("[CACHE DELETE ALL] {}", keys)
                redisTemplate.delete(keys)
            } catch (e: Exception) {
                log.error(e.message, e)
            }
        }
    }
}
