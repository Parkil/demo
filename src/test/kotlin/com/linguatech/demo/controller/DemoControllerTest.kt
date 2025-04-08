package com.linguatech.demo.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.linguatech.demo.dto.*
import com.linguatech.demo.entity.Company
import com.linguatech.demo.entity.FeatureInfo
import com.linguatech.demo.param_dto.SearchUsageLogDto
import com.linguatech.demo.param_dto.ServicePriceCreateDto
import com.linguatech.demo.param_dto.UseFeatureDto
import com.linguatech.demo.repo.CompanyRepo
import com.linguatech.demo.repo.FeatureInfoRepo
import com.linguatech.demo.repo.ServicePricingRepo
import com.linguatech.demo.repo.UsageLogRepo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*
import kotlin.test.assertEquals


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class DemoControllerTest {
    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var companyRepo: CompanyRepo
    @Autowired lateinit var featureInfoRepo: FeatureInfoRepo
    @Autowired lateinit var usageLogRepo: UsageLogRepo
    @Autowired lateinit var servicePricingRepo: ServicePricingRepo

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

        val chkEntity = servicePricingRepo.findById(convertResult.id)

        assertEquals("중복 기능 코드 테스트 요금제", convertResult.name)
        assertEquals("F_03", chkEntity.get().features[0].getCode())
        assertEquals("F_04", chkEntity.get().features[1].getCode())
    }

    @DisplayName("GET /policies/service_pricing 테스트")
    @Test
    fun findServicePricingTest() {
        val servicePriceCreateDto = ServicePriceCreateDto("일반 요금제", listOf("F_03", "F_04", "F_03"))
        val jsonStr: String = jacksonObjectMapper().writeValueAsString(servicePriceCreateDto)

        mockMvc.perform(post("/policies/service_pricing").contentType(MediaType.APPLICATION_JSON).content(jsonStr))
            .andExpect(status().isOk)

        val result: MvcResult = mockMvc.perform(get("/policies/service_pricing").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk).andReturn()

        val objectMapper = jacksonObjectMapper()
        val convertResult: List<ServicePricingDto> = objectMapper.readValue(result.response.contentAsString, object: TypeReference<List<ServicePricingDto>>() {})

        assertEquals(1, convertResult.size)
        assertEquals("일반 요금제", convertResult[0].name)
    }

    @DisplayName("PUT /companies/{companyId}/service_pricing/{servicePricingId} 테스트 - 정상")
    @Test
    fun connectServicePricingTest() {
        val objectMapper = jacksonObjectMapper()
        val servicePriceCreateDto = ServicePriceCreateDto("일반 요금제", listOf("F_03", "F_04"))
        val jsonStr: String = jacksonObjectMapper().writeValueAsString(servicePriceCreateDto)

        val createServicePricingResult = mockMvc.perform(post("/policies/service_pricing").contentType(MediaType.APPLICATION_JSON).content(jsonStr))
            .andExpect(status().isOk).andReturn()

        val createResult: ServicePricingResultDto = objectMapper.readValue(createServicePricingResult.response.contentAsString, object: TypeReference<ServicePricingResultDto>() {})

        val companyId = 1L
        val result: MvcResult = mockMvc.perform(put("/companies/$companyId/service_pricing/${createResult.id}").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk).andReturn()

        val convertResult: ConnectServicePricingResultDto = objectMapper.readValue(result.response.contentAsString, object: TypeReference<ConnectServicePricingResultDto>() {})

        assertEquals(companyId, convertResult.companyId)
        assertEquals(createResult.id, convertResult.servicePricingId)
    }

    @DisplayName("PUT /companies/{companyId}/service_pricing/{servicePricingId} 테스트 - 비정상적인 company id")
    @Test
    fun connectServicePricingInvalidCompanyIdTest() {
        val objectMapper = jacksonObjectMapper()
        val servicePriceCreateDto = ServicePriceCreateDto("일반 요금제", listOf("F_03", "F_04"))
        val jsonStr: String = jacksonObjectMapper().writeValueAsString(servicePriceCreateDto)

        val createServicePricingResult = mockMvc.perform(post("/policies/service_pricing").contentType(MediaType.APPLICATION_JSON).content(jsonStr))
            .andExpect(status().isOk).andReturn()

        val createResult: ServicePricingResultDto = objectMapper.readValue(createServicePricingResult.response.contentAsString, object: TypeReference<ServicePricingResultDto>() {})

        val result: MvcResult = mockMvc.perform(put("/companies/-999/service_pricing/${createResult.id}").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest).andReturn()

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
        val featureCode = "F_03"

        connectServicePrice(companyId, "일반 요금제", listOf("F_03", "F_04"))

        val creditArr: IntArray = getCreditArr(companyId, featureCode)
        val prevLogCount: Int = usageLogRepo.findUsageLogCount(companyId, featureCode)

        val result = useFeature(companyId, featureCode, "테스트111", status().isOk)

        val objectMapper = jacksonObjectMapper()
        val convertResult: UseFeatureResultDto =
            objectMapper.readValue(result.response.contentAsString, object : TypeReference<UseFeatureResultDto>() {})

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
        val featureCode = "F_03"
        val objectMapper = jacksonObjectMapper()

        connectServicePrice(companyId, "일반 요금제", listOf("F_03", "F_04"))

        val result = useFeature(companyId, featureCode, getTooLongText(), status().isBadRequest)
        val convertResult: ExceptionResponseDto = objectMapper.readValue(result.response.contentAsString, object: TypeReference<ExceptionResponseDto>() {})
        assertEquals("The character count limit has been exceeded. limit: 1500, actual text length: 6000", convertResult.message)
    }

    @DisplayName("POST /companies/{companyId}/feature/{featureCode} 테스트 - 서비스 요금제에 없는 기능 호출")
    @Test
    fun featureUseNotAllowedFeatureTest() {
        val companyId = 2L
        val featureCode = "F_01"
        val objectMapper = jacksonObjectMapper()

        connectServicePrice(companyId, "일반 요금제-1", listOf("F_03", "F_04"))

        val result = useFeature(companyId, featureCode, "sample text", status().isForbidden)
        val convertResult: ExceptionResponseDto = objectMapper.readValue(result.response.contentAsString, object: TypeReference<ExceptionResponseDto>() {})

        assertEquals("execution permission does not exist. feature code : F_01", convertResult.message)
    }

    @DisplayName("POST /companies/{companyId}/feature/{featureCode} 테스트 - 크레딧 부족")
    @Test
    fun featureUseShortageCreditsTest() {
        val companyId = 1L
        val featureCode = "F_04"
        val objectMapper = jacksonObjectMapper()

        connectServicePrice(companyId, "일반 요금제-2", listOf("F_04"))

        for(i: Int in 1..100)
            useFeature(companyId, featureCode, "sample text", status().isOk)

        val result: MvcResult = useFeature(companyId, featureCode, "sample text", status().isForbidden)
        val convertResult: ExceptionResponseDto = objectMapper.readValue(result.response.contentAsString, object: TypeReference<ExceptionResponseDto>() {})

        assertEquals("insufficient credits. company credits: 0", convertResult.message)
    }

    @DisplayName("POST /companies/{companyId}/feature/{featureCode} 테스트 - 중간에 요금제 변경")
    @Test
    fun featureUseChangeServicePriceTest() {
        val companyId = 1L
        val featureCode = "F_04"
        val objectMapper = jacksonObjectMapper()

        connectServicePrice(companyId, "일반 요금제-1", listOf("F_04"))
        useFeature(companyId, featureCode, "sample text", status().isOk)

        connectServicePrice(companyId, "일반 요금제-2", listOf("F_01", "F_02"))

        val result = useFeature(companyId, featureCode, "sample text", status().isForbidden)
        val convertResult: ExceptionResponseDto = objectMapper.readValue(result.response.contentAsString, object: TypeReference<ExceptionResponseDto>() {})

        assertEquals("execution permission does not exist. feature code : F_04", convertResult.message)
    }

    private fun useFeature(
        companyId: Long,
        featureCode: String,
        text: String,
        statusMatcher: ResultMatcher,
    ): MvcResult {

        val useFeatureDto = UseFeatureDto(text)
        val jsonStr: String = jacksonObjectMapper().writeValueAsString(useFeatureDto)

        val result: MvcResult = mockMvc.perform(
            post("/companies/$companyId/feature/$featureCode")
                .contentType(MediaType.APPLICATION_JSON).content(jsonStr)
        )
            .andExpect(statusMatcher).andReturn()

        return result
    }

    private fun connectServicePrice(companyId: Long, servicePriceName: String, featureCodes: List<String>) {
        val objectMapper = jacksonObjectMapper()

        val servicePriceCreateDto = ServicePriceCreateDto(servicePriceName, featureCodes)
        val servicePricingJsonStr: String = jacksonObjectMapper().writeValueAsString(servicePriceCreateDto)

        val createServicePricingResult = mockMvc.perform(
            post("/policies/service_pricing").contentType(MediaType.APPLICATION_JSON).content(servicePricingJsonStr)
        )
            .andExpect(status().isOk).andReturn()

        val createResult: ServicePricingResultDto = objectMapper.readValue(
            createServicePricingResult.response.contentAsString,
            object : TypeReference<ServicePricingResultDto>() {})

        mockMvc.perform(put("/companies/$companyId/service_pricing/${createResult.id}").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
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

    @DisplayName("GET /companies/{companyId}/usage_history 테스트")
    @Test
    fun companyHistoryTest() {
        val companyId = 1L

        connectServicePrice(companyId, "일반 요금제", listOf("F_01", "F_02", "F_03", "F_04"))

        useFeature(companyId, "F_01", "sample text-1", status().isOk)
        useFeature(companyId, "F_02", "sample text-2", status().isOk)
        useFeature(companyId, "F_03", "sample text-3", status().isOk)
        useFeature(companyId, "F_04", "sample text-4", status().isOk)

        val objectMapper = jacksonObjectMapper()
        objectMapper.registerModules(JavaTimeModule())

        val year = LocalDate.now().year
        val month = LocalDate.now().month
        val lastDay = LocalDate.now().month.maxLength()

        val searchUsageLogDto = SearchUsageLogDto(LocalDate.of(year, month, 1), LocalDate.of(year, month, lastDay), null)
        val jsonStr: String = objectMapper.writeValueAsString(searchUsageLogDto)

        val result: MvcResult = mockMvc.perform(get("/companies/$companyId/usage_history")
            .contentType(MediaType.APPLICATION_JSON).content(jsonStr))
            .andExpect(status().isOk).andReturn()

        val convertResult: StatisticsResultDto = objectMapper.readValue(result.response.contentAsString, object: TypeReference<StatisticsResultDto>() {})
        log.info("convertResult: $convertResult")
        assertEquals(4, convertResult.historyList.size)
        assertEquals("F_01", convertResult.historyList[0].featureCode)
        assertEquals("F_02", convertResult.historyList[1].featureCode)
        assertEquals("F_03", convertResult.historyList[2].featureCode)
        assertEquals("F_04", convertResult.historyList[3].featureCode)
    }
}
