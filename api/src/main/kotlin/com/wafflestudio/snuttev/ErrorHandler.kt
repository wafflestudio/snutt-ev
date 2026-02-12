package com.wafflestudio.snuttev

import com.wafflestudio.snuttev.core.common.error.ErrorInfo
import com.wafflestudio.snuttev.core.common.error.ErrorResponse
import com.wafflestudio.snuttev.core.common.error.SnuttException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.ConstraintViolationException
import org.apache.catalina.connector.ClientAbortException
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.async.AsyncRequestNotUsableException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
@RegisterReflectionForBinding(ErrorResponse::class)
class ErrorHandler {
    @ExceptionHandler(
        ConstraintViolationException::class,
        HttpMessageNotReadableException::class,
        HttpMediaTypeNotSupportedException::class,
        HttpRequestMethodNotSupportedException::class,
        MethodArgumentNotValidException::class,
        MethodArgumentTypeMismatchException::class,
        MissingServletRequestParameterException::class,
    )
    fun handleHttpMessageBadRequest(
        e: Exception,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<Any> = ResponseEntity(HttpStatus.BAD_REQUEST)

    @ExceptionHandler(ClientAbortException::class, AsyncRequestNotUsableException::class)
    fun handleClientAbortException(e: Exception): ResponseEntity<Void> = ResponseEntity.status(HttpStatus.NO_CONTENT).build()

    @ExceptionHandler(
        ObjectOptimisticLockingFailureException::class,
    )
    fun handleHttpMessageConflict(
        e: Exception,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<Any> = ResponseEntity(HttpStatus.CONFLICT)

    @ExceptionHandler(SnuttException::class)
    fun handlerSnuttException(e: SnuttException): ResponseEntity<ErrorResponse> =
        ResponseEntity(
            ErrorResponse(ErrorInfo(e.errorType.code, e.errorType.name)),
            e.errorType.httpStatus,
        )
}
