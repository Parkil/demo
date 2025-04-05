package com.linguatech.demo.config.exception_handler

import com.linguatech.demo.dto.ExceptionResponseDto
import com.linguatech.demo.exception.CustomException
import com.linguatech.demo.util.DateUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@RestControllerAdvice
class DemoExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(CustomException::class)
    fun handleCustomException(customException: CustomException): ResponseEntity<ExceptionResponseDto> {
        val status: HttpStatus = customException.getHttpStatus()

        return ResponseEntity<ExceptionResponseDto>(
            ExceptionResponseDto(
                customException.getExceptionMessage(),
                DateUtil.formatLocalDateTime(customException.getCreateAt())
            ), status
        )
    }
}