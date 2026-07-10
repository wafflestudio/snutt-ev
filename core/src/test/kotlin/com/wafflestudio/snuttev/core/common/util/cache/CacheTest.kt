package com.wafflestudio.snuttev.core.common.util.cache

import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.data.redis.core.StringRedisTemplate
import tools.jackson.databind.ObjectMapper
import java.time.Duration

class CacheTest {
    private val redisTemplate = mock(StringRedisTemplate::class.java)
    private val cache =
        Cache(
            redisTemplate = redisTemplate,
            defaultTtl = Duration.ofMinutes(5),
            objectMapper = mock(ObjectMapper::class.java),
        )

    @Test
    fun `deleteAll ignores Redis failures`() {
        `when`(redisTemplate.keys(anyString())).thenThrow(RuntimeException("Redis unavailable"))

        assertThatNoException().isThrownBy {
            cache.deleteAll(CacheKey.EVALUATIONS_BY_TAG_PAGE)
        }
    }
}
