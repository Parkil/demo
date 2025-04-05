package com.linguatech.demo.repo

import com.linguatech.demo.entity.DeductionCredit
import com.linguatech.demo.entity.FeatureInfo
import com.linguatech.demo.entity.LimitCondition
import com.linguatech.demo.enum.DeductionCriteria
import com.linguatech.demo.enum.RestrictionCriteria
import com.linguatech.demo.enum.UsageCriteria
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
class FeatureInfoRepoTest {
    @Autowired lateinit var testEntityManager: TestEntityManager
    @Autowired lateinit var featureInfoRepo: FeatureInfoRepo

    private val log = LoggerFactory.getLogger(this.javaClass)!!

    @DisplayName("입력 테스트")
    @Test
    fun insertTest() {
        val limitCondition = LimitCondition(2000, UsageCriteria.PER_ONE_USE, RestrictionCriteria.NUMBER_OF_CHAR)
        val deductionCredit = DeductionCredit(10, DeductionCriteria.PER_ONE_USE)

        val featureInfo = FeatureInfo("F_01", "AI 번역", limitCondition, deductionCredit)
        testEntityManager.persistAndFlush(featureInfo)
        testEntityManager.detach(featureInfo)

        val actual = featureInfoRepo.getReferenceById(featureInfo.getCode())
        assertEquals("AI 번역", actual.name)
    }

    @DisplayName("기능 수정 테스트")
    @Test
    fun insertDuplicateFeatureCodeTest() {
        val limitCondition = LimitCondition(2000, UsageCriteria.PER_ONE_USE, RestrictionCriteria.NUMBER_OF_CHAR)
        val deductionCredit = DeductionCredit(10, DeductionCriteria.PER_ONE_USE)

        val featureInfo = FeatureInfo("F_01", "AI 번역", limitCondition, deductionCredit)
        val updatedFeatureInfo = FeatureInfo("F_01", "또 다른 AI 번역", limitCondition, deductionCredit)

        featureInfoRepo.save(featureInfo)
        featureInfoRepo.save(updatedFeatureInfo)

        val actual = featureInfoRepo.getReferenceById("F_01")
        log.info("actual : {}", actual)

        assertEquals("또 다른 AI 번역", actual.name)
    }
}