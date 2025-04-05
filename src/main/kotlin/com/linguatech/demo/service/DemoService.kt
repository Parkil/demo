package com.linguatech.demo.service

import com.linguatech.demo.dto.CompanyDto
import com.linguatech.demo.dto.FeatureInfoDto
import com.linguatech.demo.entity.Company
import com.linguatech.demo.entity.FeatureInfo
import com.linguatech.demo.repo.CompanyRepo
import com.linguatech.demo.repo.FeatureInfoRepo
import org.springframework.stereotype.Service

@Service
class DemoService(
    val companyRepo: CompanyRepo,
    val featureInfoRepo: FeatureInfoRepo,
) {
    fun findCompanies(): List<CompanyDto> {
        val mutableList : MutableList<Company> = companyRepo.findAll()
        return mutableList.map { CompanyDto(it) }.toList()
    }

    fun findFeatures(): List<FeatureInfoDto> {
        val mutableList : MutableList<FeatureInfo> = featureInfoRepo.findAll()
        return mutableList.map { FeatureInfoDto(it) }.toList()
    }
}