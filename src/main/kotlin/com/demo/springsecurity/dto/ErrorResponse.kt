package com.demo.springsecurity.dto

import com.demo.springsecurity.common.exception.ExceptionCode

data class ErrorResponse(private val httpStatus: Int, private val errorCode: String, private val timestamp: Long, private val message: String?) {
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
