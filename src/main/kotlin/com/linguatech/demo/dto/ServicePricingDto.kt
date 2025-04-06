package com.linguatech.demo.dto

import com.linguatech.demo.entity.ServicePricing

data class ServicePricingDto(
    val id: Long,
    val name: String,
    val features: List<FeatureInfoDto>,
) {
    constructor(entity: ServicePricing): this(
        id = entity.getId(),
        name = entity.name,
        features = entity.features.map { FeatureInfoDto(it) },
    )
}