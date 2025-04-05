package com.linguatech.demo.dto

import com.linguatech.demo.entity.FeatureInfo

 data class FeatureInfoDto(
    val code: String,
    val name: String,
    val limitCondition: String,
    val deductionCredit: String,
) {
    constructor(entity: FeatureInfo) : this(
        code = entity.getCode(),
        name = entity.name,
        limitCondition = entity.limitCondition.toFormatStr(),
        deductionCredit = entity.deductionCredit.toFormatStr(),
    )
}