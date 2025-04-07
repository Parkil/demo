package com.linguatech.demo.component

import com.linguatech.demo.entity.Company
import com.linguatech.demo.entity.FeatureInfo
import com.linguatech.demo.entity.LimitCondition
import com.linguatech.demo.enum.RestrictionCriteria
import com.linguatech.demo.exception.CustomException
import com.linguatech.demo.param_dto.UseFeatureDto
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component(value = "checkPerUse")
class CheckPerUseCriteria : CheckCriteria {
    override fun check(company: Company, featureInfo: FeatureInfo, param: UseFeatureDto) {
        val limitCondition: LimitCondition = featureInfo.limitCondition
        val restrictionCriteria: RestrictionCriteria = limitCondition.restrictionCriteria

        when (restrictionCriteria) {
            RestrictionCriteria.NUMBER_OF_CHAR -> {
                val textLength: Int = param.text.length
                val amount: Int = limitCondition.limitedAmount

                if (textLength > amount) {
                    throw CustomException(
                        "The character count limit has been exceeded. limit: $amount, actual text length: $textLength",
                        HttpStatus.BAD_REQUEST
                    )
                }
            }

            RestrictionCriteria.NUMBER_OF_USE -> {
                throw CustomException(
                    "Incorrect usage criteria. For per use count criteria, only number of character criteria should be used.",
                    HttpStatus.INTERNAL_SERVER_ERROR
                )
            }
        }
    }
}