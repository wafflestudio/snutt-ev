package com.wafflestudio.snuttev.error

open class SnuttException(val errorType: ErrorType): RuntimeException(errorType.name)

object UnauthorizedException : SnuttException(ErrorType.UNAUTHORIZED)
object LectureNotFoundException : SnuttException(ErrorType.LECTURE_NOT_FOUND)
object SemesterLectureNotFoundException: SnuttException(ErrorType.SEMESTER_LECTURE_NOT_FOUND)