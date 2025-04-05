package com.linguatech.demo.repo

import com.linguatech.demo.entity.FeatureInfo
import com.linguatech.demo.entity.ServicePricing
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ServicePricingRepoTest {
    @Autowired lateinit var testEntityManager: TestEntityManager
    @Autowired lateinit var servicePricingRepo: ServicePricingRepo
    @Autowired lateinit var featureInfoRepo: FeatureInfoRepo

    private val log = LoggerFactory.getLogger(this.javaClass)!!

    @DisplayName("입력 테스트")
    @Test
    fun insertTest() {
        val pickFeatureCodes: List<String> = listOf("F_01", "F_02")
        val allFeatures: MutableList<FeatureInfo> = featureInfoRepo.findAll()

        val pickFeatures: List<FeatureInfo> = allFeatures.filter { pickFeatureCodes.contains(it.getCode()) }.toList()

        val servicePricing = ServicePricing("테스트 가격제")
        servicePricing.pickFeatures(pickFeatures)

        testEntityManager.persistAndFlush(servicePricing)
        testEntityManager.detach(servicePricing)

        val actual = servicePricingRepo.getReferenceById(servicePricing.getId())
        log.info("actual : $actual")
        assertEquals("테스트 가격제", actual.name)
    }
}