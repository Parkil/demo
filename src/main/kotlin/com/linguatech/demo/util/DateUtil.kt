package com.linguatech.demo.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object DateUtil {
    fun formatLocalDateTime(localDateTime: LocalDateTime): String {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }

    fun getCurrentMonthFirstDay(): LocalDateTime {
        return LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
    }
}
