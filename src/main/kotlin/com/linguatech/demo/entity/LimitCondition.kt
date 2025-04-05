package com.linguatech.demo.entity

import com.linguatech.demo.enum.RestrictionCriteria
import com.linguatech.demo.enum.UsageCriteria
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.hibernate.annotations.Comment

// 제한 조건
@Embeddable
class LimitCondition(
    limitedAmount: Int,
    usageCriteria: UsageCriteria,
    restrictionCriteria: RestrictionCriteria,
) {
    @Column(nullable = false)
    @Comment("제한량")
    var limitedAmount: Int = limitedAmount
        protected set

    @Column(nullable = false)
    @Comment("사용 기준")
    var usageCriteria: UsageCriteria = usageCriteria
        protected set

    @Column(nullable = false)
    @Comment("제한 기준")
    var restrictionCriteria: RestrictionCriteria = restrictionCriteria
        protected set

    fun toFormatStr(): String {
        return "$limitedAmount${restrictionCriteria.formatStr} / ${usageCriteria.formatStr}"
    }

    override fun toString(): String {
        return "LimitCondition(limitedAmount=$limitedAmount, usageCriteria=$usageCriteria, restrictionCriteria=$restrictionCriteria)"
    }
}