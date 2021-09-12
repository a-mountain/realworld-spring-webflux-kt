package com.realworld.springmongo.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException

@RestControllerAdvice
class GlobalControllerAdvice {

    @ExceptionHandler(InvalidRequestException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    fun invalidRequestExceptionHandler(e: InvalidRequestException): InvalidRequestExceptionResponse {
        val subject = e.subject
        val violation = e.violation
        val errors = mapOf(subject to listOf(violation))
        return InvalidRequestExceptionResponse(errors)
    }

    @ExceptionHandler(WebExchangeBindException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    fun constraintViolationExceptionHandler(ex: WebExchangeBindException): InvalidRequestExceptionResponse {
        val responseBody = HashMap<String, MutableList<String>>()
        for (fieldError in ex.fieldErrors) {
            val errors = responseBody.getOrPut(fieldError.field) { ArrayList() }
            errors.add(fieldError.defaultMessage ?: "")
        }
        return InvalidRequestExceptionResponse(responseBody)
    }
}

data class InvalidRequestExceptionResponse(val errors: Map<String, List<String>>)