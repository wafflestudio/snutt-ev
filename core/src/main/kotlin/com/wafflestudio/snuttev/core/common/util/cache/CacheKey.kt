package com.wafflestudio.snuttev.core.common.util.cache

import java.time.Duration

internal enum class CacheKey(
    val keyFormat: String,
    val ttl: Duration? = null,
) {
    EVALUATIONS_BY_TAG_CLASSIFICATION_PAGE("ev_by_tag_classification_cursor:%s_%s_%s_%s"),
    ;

    data class BuiltCacheKey(
        val key: String,
        val ttl: Duration? = null,
    )

    fun build(vararg args: Any?): BuiltCacheKey {
        val key = keyFormat.format(*args)
        return BuiltCacheKey(key, ttl)
    }
}
