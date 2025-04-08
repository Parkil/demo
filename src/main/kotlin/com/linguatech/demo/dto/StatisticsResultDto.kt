package com.linguatech.demo.dto

data class StatisticsResultDto(
    val companyId: Long,
    val historyList: List<UsageLogDto>,
)