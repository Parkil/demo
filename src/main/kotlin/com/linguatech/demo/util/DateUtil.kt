package com.linguatech.demo.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object DateUtil {
    fun formatLocalDateTime(localDateTime: LocalDateTime): String {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }
}
