package com.linguatech.demo.entity

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.Comment

@Entity
class FeatureInfo(
    @Id @Comment("기능 식별 코드") private val code: String,
    name: String,
    limitCondition: LimitCondition,
    deductionCredit: DeductionCredit,
) {
    @Column(nullable = false)
    @Comment("기능 명")
    var name: String = name
        protected set

    @Embedded
    var limitCondition: LimitCondition = limitCondition
        protected set

    @Embedded
    var deductionCredit: DeductionCredit = deductionCredit
        protected set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FeatureInfo

        return code == other.code
    }

    fun getCode(): String {
        return code
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }

    override fun toString(): String {
        return "FeatureInfo(code='$code', name='$name', limitCondition=$limitCondition, deductionCredit=$deductionCredit)"
    }
}