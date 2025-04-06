package com.linguatech.demo.entity

import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Entity
class Company(
    name: String,
    credits: Int,
) {
    @Id
    @Comment("회사 식별자")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long = 0L

    @Column(nullable = false)
    @Comment("회사명")
    var name: String = name
        protected set

    @Column(nullable = false)
    @Comment("보유 크레딧")
    var credits: Int = credits
        protected set

    @OneToOne(cascade = [(CascadeType.ALL)])
    protected var connectedServicePricing: ServicePricing? = null
    val servicePricing: ServicePricing? get() = connectedServicePricing

    fun connectServicePricing(servicePricing: ServicePricing) {
        connectedServicePricing = servicePricing
    }

    fun getId(): Long {
        return id
    }

    fun payFeature(featureInfo: FeatureInfo) {
        if (connectedServicePricing == null) {
            return
        }

        val chkList: List<FeatureInfo> =
            connectedServicePricing!!.features.filter { it.getCode() == featureInfo.getCode() }.toList()

        if (chkList.isNotEmpty()) {
            val payCredit: Int = chkList[0].deductionCredit.deductionCredits
            this.credits -= payCredit
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Company

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Company(id=$id, name='$name', credits=$credits, connectedServicePricing=$connectedServicePricing)"
    }
}