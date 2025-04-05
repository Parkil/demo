package com.linguatech.demo.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.linguatech.demo.dto.CompanyDto
import com.linguatech.demo.dto.ExceptionResponseDto
import com.linguatech.demo.dto.FeatureInfoDto
import com.linguatech.demo.dto.ServicePricingResultDto
import com.linguatech.demo.param_dto.ServicePriceCreateDto
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertEquals


@SpringBootTest
@AutoConfigureMockMvc
class DemoControllerTest {
    @Autowired lateinit var mockMvc: MockMvc

    @DisplayName("/companies 테스트")
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

    @DisplayName("/features 테스트")
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

    @DisplayName("/policies/service_pricing 테스트 - 정상")
    @Test
    fun createServicePolicyTest() {
        val servicePriceCreateDto = ServicePriceCreateDto("테스트 요금제111", listOf("F_03", "F_04"))
        val jsonStr: String = jacksonObjectMapper().writeValueAsString(servicePriceCreateDto)

        val result: MvcResult = mockMvc.perform(post("/policies/service_pricing").contentType(MediaType.APPLICATION_JSON).content(jsonStr))
            .andExpect(status().isOk).andReturn()

        val objectMapper = jacksonObjectMapper()
        val convertResult: ServicePricingResultDto = objectMapper.readValue(result.response.contentAsString, object: TypeReference<ServicePricingResultDto>() {})

        assertEquals("테스트 요금제111", convertResult.name)
    }

    @DisplayName("/policies/service_pricing 테스트 - 잘못된 기능 코드")
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
}
