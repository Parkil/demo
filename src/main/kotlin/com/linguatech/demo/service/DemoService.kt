package com.linguatech.demo.service

import com.linguatech.demo.component.CheckCriteria
import com.linguatech.demo.dto.*
import com.linguatech.demo.entity.Company
import com.linguatech.demo.entity.FeatureInfo
import com.linguatech.demo.entity.ServicePricing
import com.linguatech.demo.entity.UsageLog
import com.linguatech.demo.enum.UsageCriteria
import com.linguatech.demo.exception.CustomException
import com.linguatech.demo.param_dto.SearchUsageLogDto
import com.linguatech.demo.param_dto.ServicePriceCreateDto
import com.linguatech.demo.param_dto.UseFeatureDto
import com.linguatech.demo.repo.CompanyRepo
import com.linguatech.demo.repo.FeatureInfoRepo
import com.linguatech.demo.repo.ServicePricingRepo
import com.linguatech.demo.repo.UsageLogRepo
import com.linguatech.demo.specification.UsageLogSpecification
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Service
class DemoService(
    val companyRepo: CompanyRepo,
    val featureInfoRepo: FeatureInfoRepo,
    val servicePricingRepo: ServicePricingRepo,
    val usageLogRepo: UsageLogRepo,
    val checkCriteriaMap: Map<String, CheckCriteria>
) {
    private val log = LoggerFactory.getLogger(this.javaClass)!!

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    fun createServicePrice(servicePriceCreateDto: ServicePriceCreateDto): ServicePricingResultDto {
        val features: MutableList<FeatureInfo> = featureInfoRepo.findAll()
        val pickFeatures: List<FeatureInfo> = pickFeatures(servicePriceCreateDto.featureCodes, features)

        val servicePricing = ServicePricing(servicePriceCreateDto.name)
        servicePricing.pickFeatures(pickFeatures)

        val actual: ServicePricing = servicePricingRepo.save(servicePricing)
        return ServicePricingResultDto(actual.getId(), actual.name)
    }

    private fun pickFeatures(featureCodes: List<String>, features: MutableList<FeatureInfo>): List<FeatureInfo> {
        if (featureCodes.isEmpty()) {
            throw CustomException("feature code list is empty", HttpStatus.BAD_REQUEST)
        }

        val validCodes: List<String> = features.map { it.getCode() }.toList()
        val invalidCodes: List<String> = featureCodes.filter { !validCodes.contains(it) }.toList()

        if (invalidCodes.isNotEmpty()) {
            throw CustomException("invalid feature codes : $invalidCodes", HttpStatus.BAD_REQUEST)
        }

        return features.filter { featureCodes.contains(it.getCode()) }.toList()
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    fun findServicePrices(): List<ServicePricingDto> {
        val mutableList: MutableList<ServicePricing> = servicePricingRepo.findAll()
        return mutableList.map { ServicePricingDto(it) }.toList()
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    fun findCompanies(): List<CompanyDto> {
        val mutableList: MutableList<Company> = companyRepo.findAll()
        return mutableList.map { CompanyDto(it) }.toList()
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    fun findFeatures(): List<FeatureInfoDto> {
        val mutableList: MutableList<FeatureInfo> = featureInfoRepo.findAll()
        return mutableList.map { FeatureInfoDto(it) }.toList()
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    fun connectServicePricing(companyId: Long, servicePricingId: Long): ConnectServicePricingResultDto {
        val servicePricing: Optional<ServicePricing> = servicePricingRepo.findById(servicePricingId)
        val company: Company = findCompany(companyId)

        if (!servicePricing.isPresent) {
            throw CustomException("invalid service pricing id : $servicePricingId", HttpStatus.BAD_REQUEST)
        }

        company.connectServicePricing(servicePricing.get())

        return ConnectServicePricingResultDto(company.getId(), servicePricing.get().getId(), servicePricing.get().name)
    }

    private fun findCompany(companyId: Long): Company {
        val company: Optional<Company> = companyRepo.findById(companyId)

        if (!company.isPresent) {
            throw CustomException("invalid company id : $companyId", HttpStatus.BAD_REQUEST)
        }

        return company.get()
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    fun useFeature(companyId: Long, featureCode: String, param: UseFeatureDto): UseFeatureResultDto {
        var company: Company = findCompany(companyId)
        val featureInfo: FeatureInfo = findFeature(company, featureCode)

        checkCriteria(company, featureInfo, param)
        chkCreditAccount(company, featureInfo)
        runFeature(featureInfo)
        company = payCredit(company, featureInfo)
        saveUsageLog(company, featureInfo)

        return UseFeatureResultDto(company.getId(), featureInfo.getCode(), company.credits)
    }

    private fun findFeature(company: Company, featureCode: String): FeatureInfo {
        val servicePricing: ServicePricing = company.servicePricing
            ?: throw CustomException("no service price exists", HttpStatus.FORBIDDEN)

        val chkList: List<FeatureInfo> = servicePricing.features.filter { featureCode == it.getCode() }.toList()

        if (chkList.isEmpty()) {
            throw CustomException(
                "execution permission does not exist. feature code : $featureCode",
                HttpStatus.FORBIDDEN
            )
        }

        return chkList[0]
    }

    private fun checkCriteria(company: Company, featureInfo: FeatureInfo, param: UseFeatureDto) {
        val usageCriteria: UsageCriteria = featureInfo.limitCondition.usageCriteria
        val checkCriteria: CheckCriteria = checkCriteriaMap[usageCriteria.componentName]
            ?: throw CustomException(
                "invalid component name : ${usageCriteria.componentName}",
                HttpStatus.INTERNAL_SERVER_ERROR
            )

        checkCriteria.check(company, featureInfo, param)
    }

    private fun chkCreditAccount(company: Company, featureInfo: FeatureInfo) {
        val currentCredits: Int = company.credits
        val payCredits: Int = featureInfo.deductionCredit.deductionCredits

        if (payCredits > currentCredits) {
            throw CustomException("insufficient credits. company credits: $currentCredits", HttpStatus.FORBIDDEN)
        }
    }

    private fun runFeature(featureInfo: FeatureInfo) {
        log.info("run feature. feature code : ${featureInfo.getCode()}")
    }

    private fun payCredit(company: Company, featureInfo: FeatureInfo): Company {
        company.payFeature(featureInfo)
        return companyRepo.save(company)
    }

    private fun saveUsageLog(company: Company, featureInfo: FeatureInfo) {
        val usageLog = UsageLog(
            company.getId(),
            company.name,
            featureInfo.getCode(),
            featureInfo.name,
            featureInfo.deductionCredit.deductionCredits
        )
        usageLogRepo.save(usageLog)
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    fun findCompanyUsageHistory(companyId: Long, param: SearchUsageLogDto): StatisticsResultDto {
        val usageLogSpecification = UsageLogSpecification()
        val searchSpec: Specification<UsageLog> = Specification
            .where<UsageLog>(usageLogSpecification.regDtmBetween(param.startDate.atStartOfDay(), param.endDate.atStartOfDay()))
            .and(usageLogSpecification.featureCodesIn(param.featureCodes))

        val searchList: List<UsageLog> = usageLogRepo.findAll(searchSpec)
        val dtoList: List<UsageLogDto> = searchList.map{UsageLogDto(it)}.toList()

        return StatisticsResultDto(companyId, dtoList)
    }
}