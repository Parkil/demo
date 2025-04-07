package com.linguatech.demo.component

import com.linguatech.demo.entity.Company
import com.linguatech.demo.entity.FeatureInfo
import com.linguatech.demo.param_dto.UseFeatureDto

interface CheckCriteria {
    fun check(company: Company, featureInfo: FeatureInfo, param: UseFeatureDto)
}