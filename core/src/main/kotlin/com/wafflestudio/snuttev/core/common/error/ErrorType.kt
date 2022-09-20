package com.wafflestudio.snuttev.core.common.error

import org.springframework.http.HttpStatus

enum class ErrorType(
    val code: Int,
    val httpStatus: HttpStatus,
) {
    // 400
    WRONG_CURSOR_FORMAT(20001, HttpStatus.BAD_REQUEST),
    WRONG_MAIN_TAG(20002, HttpStatus.BAD_REQUEST),
    WRONG_SEARCH_TAG(20003, HttpStatus.BAD_REQUEST),

    // 401
    UNAUTHORIZED(21001, HttpStatus.UNAUTHORIZED),

    // 403
    NOT_MY_LECTURE_EVALUATION(23001, HttpStatus.FORBIDDEN),

    // 404
    LECTURE_NOT_FOUND(24001, HttpStatus.NOT_FOUND),
    SEMESTER_LECTURE_NOT_FOUND(24002, HttpStatus.NOT_FOUND),
    TAG_GROUP_NOT_FOUND(24003, HttpStatus.NOT_FOUND),
    TAG_NOT_FOUND(24004, HttpStatus.NOT_FOUND),
    LECTURE_EVALUATION_NOT_FOUND(24005, HttpStatus.NOT_FOUND),

    // 409
    EVALUATION_ALREADY_EXISTS(29001, HttpStatus.CONFLICT),
    MY_LECTURE_EVALUATION(29002, HttpStatus.CONFLICT),
    EVALUATION_REPORT_ALREADY_EXISTS(29003, HttpStatus.CONFLICT),
}