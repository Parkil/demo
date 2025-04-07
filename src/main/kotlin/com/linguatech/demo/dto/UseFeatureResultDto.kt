package com.linguatech.demo.dto

data class UseFeatureResultDto(
    val companyId: Long,
    val usedFeatureCode: String,
    val reserveCredits: Int,
)