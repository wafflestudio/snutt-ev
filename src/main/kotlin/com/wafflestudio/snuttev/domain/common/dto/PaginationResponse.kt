package com.wafflestudio.snuttev.domain.common.dto


abstract class CursorPaginationResponse (
    open val content: List<Any>,

    open val cursor: String?,

    open val size: Int,

    open val last: Boolean,

    open val totalCount: Long? = null
)

abstract class OffsetPaginationResponse (
    open val content: List<Any>,

    open val page: Long,

    open val size: Int,

    open val last: Boolean,

    open val totalCount: Long? = null
)
