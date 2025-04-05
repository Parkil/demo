package com.linguatech.demo.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
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
        val convertList : List<Map<String, Any>> = objectMapper.readValue(result.response.contentAsString, object: TypeReference<List<Map<String, Any>>>() {})

        assertEquals("A사", convertList[0]["name"])
        assertEquals("B사", convertList[1]["name"])
        assertEquals("C사", convertList[2]["name"])
    }

    @DisplayName("/features 테스트")
    @Test
    fun featuresTest() {
        val result: MvcResult = mockMvc.perform(get("/features").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk).andReturn()

        print(result.response.contentAsString)

//        val objectMapper = jacksonObjectMapper()
//        val convertList : List<Map<String, Any>> = objectMapper.readValue(result.response.contentAsString, object: TypeReference<List<Map<String, Any>>>() {})

    }
}