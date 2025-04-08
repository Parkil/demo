package com.linguatech.demo.param_dto

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class SearchUsageLogDto(
    @DateTimeFormat(pattern = "yyyy-MM-dd") val startDate: LocalDate,
    @DateTimeFormat(pattern = "yyyy-MM-dd") val endDate: LocalDate,
    val featureCodes: List<String>?,
)