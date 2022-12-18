package com.wafflestudio.snuttev.core.common.util.cache

import java.time.Duration

internal enum class CacheKey(
    val keyFormat: String,
    val ttl: Duration? = null,
) {
    EVALUATIONS_BY_TAG_PAGE("ev_by_tag_cursor:%s_%s_%s"),
    MAIN_TAGS("main_tags", Duration.ofHours(1)),
    SEARCH_TAGS("search_tags", Duration.ofHours(1)),
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
