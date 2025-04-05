package com.linguatech.demo.entity

import com.linguatech.demo.enum.DeductionCriteria
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.hibernate.annotations.Comment

// 크레딧 차감
@Embeddable
class DeductionCredit(
    deductionCredits: Int,
    deductionCriteria: DeductionCriteria,
) {
    @Column(nullable = false)
    @Comment("차감되는 크레딧")
    var deductionCredits: Int = deductionCredits
        protected set

    @Column(nullable = false)
    @Comment("크레딧 차감 기준")
    var deductionCriteria: DeductionCriteria = deductionCriteria
        protected set

    fun toFormatStr(): String {
        return "$deductionCredits 크레딧 / ${deductionCriteria.formatStr}"
    }

    override fun toString(): String {
        return "DeductionCredit(deductionCredits=$deductionCredits, deductionCriteria=$deductionCriteria)"
    }
}