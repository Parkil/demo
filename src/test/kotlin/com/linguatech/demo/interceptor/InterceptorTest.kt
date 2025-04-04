package com.linguatech.demo.interceptor

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest
@AutoConfigureMockMvc
class InterceptorTest {
    @Autowired lateinit var mockMvc: MockMvc

    @DisplayName("기능 수행 시 관련된 Interceptor 가 정상적으로 작동하는지 테스트")
    @Test
    fun featureInterceptorTest() {
        mockMvc.perform(post("/companies/1/feature/1").contentType(MediaType.APPLICATION_JSON).content("{\n" +
                "  \"id\": \"999\",\n" +
                "  \"value\": \"content\"\n" +
                "}"))
            .andExpect(status().isOk)
    }
}