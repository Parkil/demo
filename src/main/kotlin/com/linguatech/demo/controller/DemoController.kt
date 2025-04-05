package com.linguatech.demo.controller

import com.linguatech.demo.dto.CompanyDto
import com.linguatech.demo.dto.FeatureInfoDto
import com.linguatech.demo.dto.ServicePricingResultDto
import com.linguatech.demo.param_dto.ServicePriceCreateDto
import com.linguatech.demo.service.DemoService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
class DemoController(
    private val demoService: DemoService,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)!!

    //서비스 요금제 생성
    @PostMapping(value = ["/policies/service_pricing"])
    fun createServicePricing(
        @RequestBody param: ServicePriceCreateDto,
    ): ResponseEntity<ServicePricingResultDto> {
        return ResponseEntity<ServicePricingResultDto>(demoService.createServicePrice(param), HttpStatus.OK)
    }

    //서비스 요금제 조회
    @GetMapping(value = ["/policies/service_pricing"])
    fun findServicePricing(): ResponseEntity<String> {
        return ResponseEntity<String>("findServicePricing", HttpStatus.OK)
    }

    //기업-서비스 요금제 연결
    @PutMapping(value = ["/companies/{companyId}/service_pricing/{servicePricingId}"])
    fun connectServicePricing(
        @RequestBody param: Map<String, String>,
        @PathVariable companyId: String,
        @PathVariable servicePricingId: String,
    ): ResponseEntity<String> {
        log.info("connectServicePricing param : {}", param)
        log.info("companyId param : {}", companyId)
        log.info("servicePricingId param : {}", servicePricingId)
        return ResponseEntity<String>("connectServicePricing", HttpStatus.OK)
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
    @PostMapping(value = ["/companies/{companyId}/feature/{featureId}"])
    fun useFeature(
        @RequestBody param: Map<String, String>,
        @PathVariable companyId: String,
        @PathVariable featureId: String,
    ): ResponseEntity<String> {
        log.info("useFeature param : {}", param)
        log.info("companyId : {}", companyId)
        log.info("featureId : {}", featureId)

        val dummyKey: String? = param.get("id")

        if("error".equals(dummyKey)){
            throw RuntimeException("고의로 일으킨 예외")
        }

        return ResponseEntity<String>("useFeature", HttpStatus.OK)
    }

    //사용 통계 조회
    @GetMapping(value = ["/companies/{companyId}/statistics"])
    fun findCompanyStatistics(
        @RequestBody logParamDto: Map<String, String>,
        @PathVariable companyId: String,
    ): ResponseEntity<String> {
        log.info("findCompanyStatistics param : {}", logParamDto)
        return ResponseEntity<String>("findCompanyStatistics", HttpStatus.OK)
    }
}