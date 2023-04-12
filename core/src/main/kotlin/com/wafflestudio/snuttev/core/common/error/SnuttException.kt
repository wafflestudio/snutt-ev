package com.wafflestudio.snuttev.core.common.error

open class SnuttException(val errorType: ErrorType) : RuntimeException(errorType.name)

object UnauthorizedException : SnuttException(ErrorType.UNAUTHORIZED)
object LectureNotFoundException : SnuttException(ErrorType.LECTURE_NOT_FOUND)
object SemesterLectureNotFoundException : SnuttException(ErrorType.SEMESTER_LECTURE_NOT_FOUND)
object WrongCursorFormatException : SnuttException(ErrorType.WRONG_CURSOR_FORMAT)
object TagGroupNotFoundException : SnuttException(ErrorType.TAG_GROUP_NOT_FOUND)
object TagNotFoundException : SnuttException(ErrorType.TAG_NOT_FOUND)
object EvaluationAlreadyExistsException : SnuttException(ErrorType.EVALUATION_ALREADY_EXISTS)
object LectureEvaluationNotFoundException : SnuttException(ErrorType.LECTURE_EVALUATION_NOT_FOUND)
object NotMyLectureEvaluationException : SnuttException(ErrorType.NOT_MY_LECTURE_EVALUATION)
object MyLectureEvaluationException : SnuttException(ErrorType.MY_LECTURE_EVALUATION)
object EvaluationReportAlreadyExistsException : SnuttException(ErrorType.EVALUATION_REPORT_ALREADY_EXISTS)
object EvaluationLikeAlreadyExistsException : SnuttException(ErrorType.EVALUATION_LIKE_ALREADY_EXISTS)
object EvaluationLikeAlreadyNotExistsException : SnuttException(ErrorType.EVALUATION_LIKE_ALREADY_NOT_EXISTS)
object EvaluationContentBlankException : SnuttException(ErrorType.EVALUATION_CONTENT_BLANK)
