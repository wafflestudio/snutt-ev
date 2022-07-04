package com.wafflestudio.snuttev.common.error

data class ErrorResponse(
    val error: ErrorInfo
)

data class ErrorInfo(
    val code: Int,
    val message: String,
)
