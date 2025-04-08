package com.linguatech.demo.dto

import com.linguatech.demo.entity.UsageLog
import com.linguatech.demo.util.DateUtil

data class UsageLogDto(
    val companyName: String,
    val featureCode: String,
    val featureName: String,
    val usageCredit: Int,
    val regDtm: String
) {
    constructor(entity: UsageLog) : this(
        companyName = entity.companyName,
        featureCode = entity.featureCode,
        featureName = entity.featureName,
        usageCredit = entity.usageCredit,
        regDtm = DateUtil.formatLocalDateTime(entity.regDtm.toLocalDateTime())
    )
}