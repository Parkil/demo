package com.linguatech.demo.dto

import com.linguatech.demo.entity.Company

data class CompanyDto(
    val id: Long,
    val name: String,
    val currentServicePrice: Map<String, Any>,
) {
    constructor(entity: Company) : this(
        id = entity.getId(),
        name = entity.name,
        currentServicePrice = if (entity.servicePricing != null) {
            mapOf("id" to entity.servicePricing!!.getId(), "name" to entity.servicePricing!!.name)
        } else {
            mapOf("id" to -99, "name" to "none")
        }
    )
}