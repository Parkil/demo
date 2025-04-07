package com.linguatech.demo.component

import com.linguatech.demo.entity.Company
import com.linguatech.demo.entity.FeatureInfo
import com.linguatech.demo.entity.LimitCondition
import com.linguatech.demo.enum.RestrictionCriteria
import com.linguatech.demo.exception.CustomException
import com.linguatech.demo.param_dto.UseFeatureDto
import com.linguatech.demo.repo.UsageLogRepo
import com.linguatech.demo.util.DateUtil
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component(value = "checkPerMonth")
class CheckPerMonthCriteria(
    private val usageLogRepo: UsageLogRepo
) : CheckCriteria {
    override fun check(company: Company, featureInfo: FeatureInfo, param: UseFeatureDto) {
        val limitCondition: LimitCondition = featureInfo.limitCondition
        val restrictionCriteria: RestrictionCriteria = limitCondition.restrictionCriteria

        when (restrictionCriteria) {
            RestrictionCriteria.NUMBER_OF_CHAR -> {
                throw CustomException(
                    "Incorrect usage criteria. For per one month criteria, only number of use criteria should be used.",
                    HttpStatus.INTERNAL_SERVER_ERROR
                )
            }

            RestrictionCriteria.NUMBER_OF_USE -> {
                val code = featureInfo.getCode()
                val currentUsageCountInMonth: Int =
                    usageLogRepo.findUsageLogCount(company.getId(), code, DateUtil.getCurrentMonthFirstDay())
                val monthlyUsageCountLimit: Int = featureInfo.limitCondition.limitedAmount

                if (currentUsageCountInMonth > monthlyUsageCountLimit) {
                    throw CustomException(
                        "The monthly usage limit has been exceeded. limit: $monthlyUsageCountLimit",
                        HttpStatus.BAD_REQUEST
                    )
                }
            }
        }
    }
}