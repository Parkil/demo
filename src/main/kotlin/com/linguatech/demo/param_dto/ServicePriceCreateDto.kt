package com.linguatech.demo.param_dto

data class ServicePriceCreateDto(
    val name: String,
    val featureCodes: List<String>,
)