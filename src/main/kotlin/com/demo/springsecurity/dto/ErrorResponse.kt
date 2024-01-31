package com.demo.springsecurity.dto

import com.demo.springsecurity.common.exception.ExceptionCode

data class ErrorResponse(private val httpStatus: Int, private val errorCode: String, private val message: String?, private val timestamp: Long) {
    constructor(exceptionCode: ExceptionCode): this(exceptionCode.httpStatus.value(), exceptionCode.code, exceptionCode.message, System.currentTimeMillis())
    constructor(exceptionCode: ExceptionCode, customMessage: String): this(exceptionCode.httpStatus.value(), exceptionCode.code, customMessage, System.currentTimeMillis())

    companion object {
        fun from(exceptionCode: ExceptionCode) = ErrorResponse(
            httpStatus = exceptionCode.httpStatus.value(),
            errorCode = exceptionCode.code,
            message = exceptionCode.message,
            timestamp = System.currentTimeMillis()
        )

        fun from(exceptionCode: ExceptionCode, customMessage: String) = ErrorResponse(
            httpStatus = exceptionCode.httpStatus.value(),
            errorCode = exceptionCode.code,
            message = customMessage,
            timestamp = System.currentTimeMillis()
        )
    }
}
