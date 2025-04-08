package com.linguatech.demo.specification

import com.linguatech.demo.entity.UsageLog
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDateTime


class UsageLogSpecification {
    fun regDtmBetween(startDtm: LocalDateTime, endDtm: LocalDateTime): Specification<UsageLog?> {
        return Specification<UsageLog?> { root: Root<UsageLog?>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            criteriaBuilder.between(root.get("regDtm"), startDtm, endDtm)
        }
    }

    fun featureCodesIn(featureCodes: List<String>?): Specification<UsageLog?> {
        return Specification<UsageLog?> { root: Root<UsageLog?>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            if (featureCodes.isNullOrEmpty()) {
                return@Specification criteriaBuilder.conjunction()
            } else {
                return@Specification root.get<Any>("featureCode").`in`(featureCodes)
            }
        }
    }
}