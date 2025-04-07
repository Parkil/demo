package com.linguatech.demo.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.linguatech.demo.dto.*
import com.linguatech.demo.entity.Company
import com.linguatech.demo.entity.FeatureInfo
import com.linguatech.demo.param_dto.ServicePriceCreateDto
import com.linguatech.demo.param_dto.UseFeatureDto
import com.linguatech.demo.repo.CompanyRepo
import com.linguatech.demo.repo.FeatureInfoRepo
import com.linguatech.demo.repo.UsageLogRepo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


@SpringBootTest
@AutoConfigureMockMvc
class DemoControllerTest {
    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var companyRepo: CompanyRepo
    @Autowired lateinit var featureInfoRepo: FeatureInfoRepo
    @Autowired lateinit var usageLogRepo: UsageLogRepo

    private val log = LoggerFactory.getLogger(this.javaClass)!!

    @DisplayName("GET /companies 테스트")
    @Test
    fun companiesTest() {
        val result: MvcResult = mockMvc.perform(get("/companies").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk).andReturn()


        val objectMapper = jacksonObjectMapper()
        val convertList : List<CompanyDto> = objectMapper.readValue(result.response.contentAsString, object: TypeReference<List<CompanyDto>>() {})

        assertEquals("A사", convertList[0].name)
        assertEquals("B사", convertList[1].name)
        assertEquals("C사", convertList[2].name)
    }

    @DisplayName("GET /features 테스트")
    @Test
    fun featuresTest() {
        val result: MvcResult = mockMvc.perform(get("/features").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk).andReturn()

        val objectMapper = jacksonObjectMapper()
        val convertList : List<FeatureInfoDto> = objectMapper.readValue(result.response.contentAsString, object: TypeReference<List<FeatureInfoDto>>() {})

        assertEquals("AI 번역", convertList[0].name)
        assertEquals("AI 교정", convertList[1].name)
        assertEquals("AI 뉘앙스 조절", convertList[2].name)
        assertEquals("AI 초안 작성", convertList[3].name)
    }

    @DisplayName("POST /policies/service_pricing 테스트 - 정상")
    @Test
    fun createServicePolicyTest() {
        val servicePriceCreateDto = ServicePriceCreateDto("일반 요금제", listOf("F_01", "F_02", "F_04"))
        val jsonStr: String = jacksonObjectMapper().writeValueAsString(servicePriceCreateDto)

        val result: MvcResult = mockMvc.perform(post("/policies/service_pricing").contentType(MediaType.APPLICATION_JSON).content(jsonStr))
            .andExpect(status().isOk).andReturn()

        val objectMapper = jacksonObjectMapper()
        val convertResult: ServicePricingResultDto = objectMapper.readValue(result.response.contentAsString, object: TypeReference<ServicePricingResultDto>() {})

        assertEquals("일반 요금제", convertResult.name)
    }

    @DisplayName("POST /policies/service_pricing 테스트 - 잘못된 기능 코드")
    @Test
    fun createServicePolicyWrongFeatureCodeTest() {
        val servicePriceCreateDto = ServicePriceCreateDto("테스트 요금제", listOf("F_03", "F_04", "F_999"))
        val jsonStr: String = jacksonObjectMapper().writeValueAsString(servicePriceCreateDto)

        val result: MvcResult = mockMvc.perform(post("/policies/service_pricing").contentType(MediaType.APPLICATION_JSON).content(jsonStr))
            .andExpect(status().isBadRequest).andReturn()

        val objectMapper = jacksonObjectMapper()
        val convertResult: ExceptionResponseDto = objectMapper.readValue(result.response.contentAsString, object: TypeReference<ExceptionResponseDto>() {})

        assertEquals("invalid feature codes : [F_999]", convertResult.message)
    }

    @DisplayName("POST /policies/service_pricing 테스트 - 기능 코드 없음")
    @Test
    fun createServicePolicyEmptyFeatureCodeTest() {
        val servicePriceCreateDto = ServicePriceCreateDto("테스트 요금제", mutableListOf())
        val jsonStr: String = jacksonObjectMapper().writeValueAsString(servicePriceCreateDto)

        val result: MvcResult = mockMvc.perform(post("/policies/service_pricing").contentType(MediaType.APPLICATION_JSON).content(jsonStr))
            .andExpect(status().isBadRequest).andReturn()

        val objectMapper = jacksonObjectMapper()
        val convertResult: ExceptionResponseDto = objectMapper.readValue(result.response.contentAsString, object: TypeReference<ExceptionResponseDto>() {})

        assertEquals("feature code list is empty", convertResult.message)
    }

    @DisplayName("POST /policies/service_pricing 테스트 - 중복된 기능 코드")
    @Test
    fun createServicePolicyDuplicateFeatureCodeTest() {
        val servicePriceCreateDto = ServicePriceCreateDto("중복 기능 코드 테스트 요금제", listOf("F_03", "F_04", "F_03"))
        val jsonStr: String = jacksonObjectMapper().writeValueAsString(servicePriceCreateDto)

        val result: MvcResult = mockMvc.perform(post("/policies/service_pricing").contentType(MediaType.APPLICATION_JSON).content(jsonStr))
            .andExpect(status().isOk).andReturn()

        val objectMapper = jacksonObjectMapper()
        val convertResult: ServicePricingResultDto = objectMapper.readValue(result.response.contentAsString, object: TypeReference<ServicePricingResultDto>() {})

        assertEquals("중복 기능 코드 테스트 요금제", convertResult.name)
    }


    @DisplayName("GET /policies/service_pricing 테스트")
    @Test
    fun findServicePricingTest() {
        val result: MvcResult = mockMvc.perform(get("/policies/service_pricing").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk).andReturn()

        val objectMapper = jacksonObjectMapper()
        val convertResult: List<ServicePricingDto> = objectMapper.readValue(result.response.contentAsString, object: TypeReference<List<ServicePricingDto>>() {})

        log.info("result: $convertResult")

        assertNotEquals(0, convertResult.size)
    }

    @DisplayName("PUT /companies/{companyId}/service_pricing/{servicePricingId} 테스트 - 정상")
    @Test
    fun connectServicePricingTest() {
        val result: MvcResult = mockMvc.perform(put("/companies/1/service_pricing/1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk).andReturn()

        val objectMapper = jacksonObjectMapper()
        val convertResult: ConnectServicePricingResultDto = objectMapper.readValue(result.response.contentAsString, object: TypeReference<ConnectServicePricingResultDto>() {})

        assertEquals(1, convertResult.companyId)
        assertEquals(1, convertResult.servicePricingId)
    }

    @DisplayName("PUT /companies/{companyId}/service_pricing/{servicePricingId} 테스트 - 비정상적인 company id")
    @Test
    fun connectServicePricingInvalidCompanyIdTest() {
        val result: MvcResult = mockMvc.perform(put("/companies/-999/service_pricing/1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest).andReturn()

        val objectMapper = jacksonObjectMapper()
        val convertResult: ExceptionResponseDto = objectMapper.readValue(result.response.contentAsString, object: TypeReference<ExceptionResponseDto>() {})

        assertEquals("invalid company id : -999", convertResult.message)
    }

    @DisplayName("PUT /companies/{companyId}/service_pricing/{servicePricingId} 테스트 - 비정상적인 servicePricing id")
    @Test
    fun connectServicePricingInvalidServicePricingTest() {
        val result: MvcResult = mockMvc.perform(put("/companies/1/service_pricing/-999").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest).andReturn()

        val objectMapper = jacksonObjectMapper()
        val convertResult: ExceptionResponseDto = objectMapper.readValue(result.response.contentAsString, object: TypeReference<ExceptionResponseDto>() {})

        assertEquals("invalid service pricing id : -999", convertResult.message)
    }

    @DisplayName("POST /companies/{companyId}/feature/{featureCode} 테스트 - 정상")
    @Test
    fun featureUseTest() {
        val companyId = 1L
        val featureCode = "F_01"
        val creditArr: IntArray = getCreditArr(companyId, featureCode)
        val prevLogCount: Int = usageLogRepo.findUsageLogCount(companyId, featureCode)

        val useFeatureDto = UseFeatureDto("텍스트 111")
        val jsonStr: String = jacksonObjectMapper().writeValueAsString(useFeatureDto)

        val result: MvcResult = mockMvc.perform(post("/companies/$companyId/feature/$featureCode")
            .contentType(MediaType.APPLICATION_JSON).content(jsonStr))
            .andExpect(status().isOk).andReturn()

        val objectMapper = jacksonObjectMapper()
        val convertResult: UseFeatureResultDto = objectMapper.readValue(result.response.contentAsString, object: TypeReference<UseFeatureResultDto>() {})

        log.info("use feature result: $convertResult")

        val chkCredits = creditArr[0] - creditArr[1]
        assertEquals(convertResult.reserveCredits, chkCredits)

        val afterLogCount: Int = usageLogRepo.findUsageLogCount(companyId, featureCode)
        assertEquals(afterLogCount, prevLogCount + 1)
    }

    @DisplayName("POST /companies/{companyId}/feature/{featureCode} 테스트 - 텍스트 길이 초과")
    @Test
    fun featureUseTooLongTextTest() {
        val companyId = 1L
        val featureCode = "F_01"

        val useFeatureDto = UseFeatureDto(getTooLongText())
        val jsonStr: String = jacksonObjectMapper().writeValueAsString(useFeatureDto)

        val result: MvcResult = mockMvc.perform(post("/companies/$companyId/feature/$featureCode")
            .contentType(MediaType.APPLICATION_JSON).content(jsonStr))
            .andExpect(status().isBadRequest).andReturn()

        val objectMapper = jacksonObjectMapper()
        val convertResult: ExceptionResponseDto = objectMapper.readValue(result.response.contentAsString, object: TypeReference<ExceptionResponseDto>() {})

        assertEquals("The character count limit has been exceeded. limit: 2000, actual text length: 6000", convertResult.message)
    }

    @DisplayName("POST /companies/{companyId}/feature/{featureCode} 테스트 - 서비스 요금제에 없는 기능 호출")
    @Test
    fun featureUseNotAllowedFeatureTest() {
        val companyId = 1L
        val featureCode = "F_03"

        val useFeatureDto = UseFeatureDto("sample text")
        val jsonStr: String = jacksonObjectMapper().writeValueAsString(useFeatureDto)

        val result: MvcResult = mockMvc.perform(post("/companies/$companyId/feature/$featureCode")
            .contentType(MediaType.APPLICATION_JSON).content(jsonStr))
            .andExpect(status().isForbidden).andReturn()

        val objectMapper = jacksonObjectMapper()
        val convertResult: ExceptionResponseDto = objectMapper.readValue(result.response.contentAsString, object: TypeReference<ExceptionResponseDto>() {})

        assertEquals("execution permission does not exist. feature code : F_03", convertResult.message)
    }

    private fun getCreditArr(companyId: Long, featureCode: String): IntArray {
        val company: Optional<Company> = companyRepo.findById(companyId)
        val featureInfo: Optional<FeatureInfo> = featureInfoRepo.findById(featureCode)

        val orgCredits: Int = company.get().credits
        val deductionCredits: Int = featureInfo.get().deductionCredit.deductionCredits

        val array = IntArray(2)
        array[0] = orgCredits
        array[1] = deductionCredits
        return array
    }

    private fun getTooLongText(): String {
        var str = ""
        for(i: Int in 1..6000)
            str += "1"

        return str
    }
}
