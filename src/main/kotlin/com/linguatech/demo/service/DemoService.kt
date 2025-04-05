package com.linguatech.demo.service

import com.linguatech.demo.dto.CompanyDto
import com.linguatech.demo.dto.FeatureInfoDto
import com.linguatech.demo.dto.ServicePricingResultDto
import com.linguatech.demo.entity.Company
import com.linguatech.demo.entity.FeatureInfo
import com.linguatech.demo.entity.ServicePricing
import com.linguatech.demo.param_dto.ServicePriceCreateDto
import com.linguatech.demo.repo.CompanyRepo
import com.linguatech.demo.repo.FeatureInfoRepo
import com.linguatech.demo.repo.ServicePricingRepo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DemoService(
    val companyRepo: CompanyRepo,
    val featureInfoRepo: FeatureInfoRepo,
    val servicePricingRepo: ServicePricingRepo
) {
    @Transactional
    fun createServicePrice(servicePriceCreateDto: ServicePriceCreateDto): ServicePricingResultDto {
        val features : MutableList<FeatureInfo> = featureInfoRepo.findAll()
        val pickFeatures: List<FeatureInfo> = pickFeatures(servicePriceCreateDto.featureCodes, features)

        val servicePricing = ServicePricing(servicePriceCreateDto.name)
        servicePricing.pickFeatures(pickFeatures)

        val actual: ServicePricing = servicePricingRepo.save(servicePricing)
        return ServicePricingResultDto(actual.getId(), actual.name)
    }

    private fun pickFeatures(featureCodes: List<String>, features: MutableList<FeatureInfo>): List<FeatureInfo> {
        if (featureCodes.isEmpty()) {
            throw RuntimeException("feature code list is empty")
        }

        val validCodes: List<String> = features.map { it.getCode() }.toList()
        val invalidCodes: List<String> = featureCodes.filter { !validCodes.contains(it) }.toList()

        if (invalidCodes.isNotEmpty()) {
            throw RuntimeException("invalid feature codes : $invalidCodes")
        }

        return features.filter { featureCodes.contains(it.getCode()) }.toList()
    }

    fun findCompanies(): List<CompanyDto> {
        val mutableList : MutableList<Company> = companyRepo.findAll()
        return mutableList.map { CompanyDto(it) }.toList()
    }

    fun findFeatures(): List<FeatureInfoDto> {
        val mutableList : MutableList<FeatureInfo> = featureInfoRepo.findAll()
        return mutableList.map { FeatureInfoDto(it) }.toList()
    }
}