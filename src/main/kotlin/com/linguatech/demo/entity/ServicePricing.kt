package com.linguatech.demo.entity

import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Entity
class ServicePricing(
    name: String,
) {
    @Id
    @Comment("식별자")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long = 0L

    @Column(nullable = false)
    @Comment("서비스 요금제 명")
    var name: String = name
        protected set

    @OneToMany(fetch = FetchType.LAZY, cascade = [(CascadeType.ALL)])
    protected val featureList: MutableList<FeatureInfo> = mutableListOf()
    val features: List<FeatureInfo> get() = featureList.toList()

    fun pickFeatures(features: List<FeatureInfo>) {
        featureList.addAll(features)
    }

    fun getId(): Long {
        return id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServicePricing

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}