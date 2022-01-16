package com.wafflestudio.snuttev.error

import org.springframework.http.HttpStatus

enum class ErrorType(
    val code: Int,
    val httpStatus: HttpStatus,
) {
    // 400
    WRONG_CURSOR_FORMAT(20001, HttpStatus.BAD_REQUEST),

    // 401
    UNAUTHORIZED(21001, HttpStatus.UNAUTHORIZED),

    // 404
    LECTURE_NOT_FOUND(24001, HttpStatus.NOT_FOUND),
    SEMESTER_LECTURE_NOT_FOUND(24002, HttpStatus.NOT_FOUND),
}
