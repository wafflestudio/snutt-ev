package com.wafflestudio.snuttev.core.common.type

enum class Semester(
    val value: Int,
    val label: String,
) {
    SPRING(1, "1"),
    SUMMER(2, "여름"),
    AUTUMN(3, "2"),
    WINTER(4, "겨울"),
    ;

    companion object {
        fun labelOfOrNull(value: Int): String? = entries.firstOrNull { it.value == value }?.label
    }
}
