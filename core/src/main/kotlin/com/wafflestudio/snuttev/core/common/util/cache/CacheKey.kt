package com.wafflestudio.snuttev.core.common.util.cache

import java.time.Duration

enum class CacheKey(
    val key: String,
    val ttl: Duration,
) {
    EVALUATIONS_BY_TAG_CLASSIFICATION_PAGE(
        "ev_by_tag_classification_cursor:%s_%s_%s_%s",
        Duration.ofMinutes(2)
    ),
}
