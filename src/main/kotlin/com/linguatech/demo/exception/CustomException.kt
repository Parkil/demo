package com.linguatech.demo.exception

import org.springframework.http.HttpStatus
import java.time.LocalDateTime


class CustomException(private val exceptionMessage: String, private val httpStatus: HttpStatus) : RuntimeException() {
    private val createAt: LocalDateTime = LocalDateTime.now()

    fun getExceptionMessage(): String {
        return exceptionMessage
    }

    fun getHttpStatus(): HttpStatus {
        return httpStatus
    }

    fun getCreateAt(): LocalDateTime {
        return createAt
    }
}
