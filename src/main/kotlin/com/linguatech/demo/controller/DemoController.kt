package com.linguatech.demo.controller

import com.linguatech.demo.dto.*
import com.linguatech.demo.param_dto.SearchUsageLogDto
import com.linguatech.demo.param_dto.ServicePriceCreateDto
import com.linguatech.demo.param_dto.UseFeatureDto
import com.linguatech.demo.service.DemoService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
class DemoController(
    private val demoService: DemoService,
) {
    //서비스 요금제 생성
    @PostMapping(value = ["/policies/service_pricing"])
    fun createServicePricing(
        @RequestBody param: ServicePriceCreateDto,
    ): ResponseEntity<ServicePricingResultDto> {
        return ResponseEntity<ServicePricingResultDto>(demoService.createServicePrice(param), HttpStatus.OK)
    }

    //서비스 요금제 조회
    @GetMapping(value = ["/policies/service_pricing"])
    fun findServicePricing(): ResponseEntity<List<ServicePricingDto>> {
        return ResponseEntity<List<ServicePricingDto>>(demoService.findServicePrices(), HttpStatus.OK)
    }

    //기업-서비스 요금제 연결
    @PutMapping(value = ["/companies/{companyId}/service_pricing/{servicePricingId}"])
    fun connectServicePricing(
        @PathVariable companyId: Long,
        @PathVariable servicePricingId: Long,
    ): ResponseEntity<ConnectServicePricingResultDto> {
        return ResponseEntity<ConnectServicePricingResultDto>(demoService.connectServicePricing(companyId, servicePricingId), HttpStatus.OK)
    }

    //회사 정보 조회
    @GetMapping(value = ["/companies"])
    fun findCompanies(): ResponseEntity<List<CompanyDto>> {
        return ResponseEntity<List<CompanyDto>>(demoService.findCompanies(), HttpStatus.OK)
    }

    //기본 기능 리스트 조회
    @GetMapping(value = ["/features"])
    fun findFeatures(): ResponseEntity<List<FeatureInfoDto>> {
        return ResponseEntity<List<FeatureInfoDto>>(demoService.findFeatures(), HttpStatus.OK)
    }

    //기능 사용 처리
    @PostMapping(value = ["/companies/{companyId}/feature/{featureCode}"])
    fun useFeature(
        @PathVariable companyId: Long,
        @PathVariable featureCode: String,
        @RequestBody param: UseFeatureDto,
    ): ResponseEntity<UseFeatureResultDto> {
        return ResponseEntity<UseFeatureResultDto>(demoService.useFeature(companyId, featureCode, param), HttpStatus.OK)
    }

    //사용 내역 조회
    @GetMapping(value = ["/companies/{companyId}/usage_history"])
    fun findCompanyUsageHistory(
        @PathVariable companyId: Long,
        @RequestBody param: SearchUsageLogDto,
    ): ResponseEntity<StatisticsResultDto> {
        return ResponseEntity<StatisticsResultDto>(demoService.findCompanyUsageHistory(companyId, param), HttpStatus.OK)
    }
}