package com.linguatech.demo.entity

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.sql.Timestamp
import java.time.LocalDateTime

@Entity
class UsageLog(
    companyId: Long,
    featureCode: String,
    featureName: String,
    usageCredit: Int,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long = 0L

    @Column(nullable = false)
    @Comment("회사 ID")
    var companyId: Long = companyId
        protected set

    @Column(nullable = false)
    @Comment("기능 코드")
    var featureCode: String = featureCode
        protected set

    @Column(nullable = false)
    @Comment("기능 명")
    var featureName: String = featureName
        protected set

    @Column(nullable = false)
    @Comment("사용 크레딧")
    var usageCredit: Int = usageCredit
        protected set

    @Temporal(TemporalType.TIMESTAMP)
    var regDtm: Timestamp = Timestamp.valueOf(LocalDateTime.now())
        protected set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UsageLog

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "UsageLog(id=$id, companyId=$companyId, featureCode='$featureCode', featureName='$featureName', usageCredit=$usageCredit, regDtm=$regDtm)"
    }
}