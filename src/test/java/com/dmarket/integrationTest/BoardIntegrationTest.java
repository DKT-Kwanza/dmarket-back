package com.dmarket.integrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import com.dmarket.jwt.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@Transactional
public class BoardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JWTUtil jwtUtil;

    private String token;

    @BeforeEach
    public void setUp() {
        token = "Bearer " + createTestToken();
    }

    private String createTestToken() {

        return jwtUtil.createAccessJwt(9L, "dbsrl1026@gachon.ac.kr", "ROLE_GM");
    }

    @Test
    @DisplayName("공지사항 조회")
    public void getNoticesForUser() throws Exception {

        mockMvc.perform(get("/api/board/notice")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-notices-for-user"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("FAQ 조회")
    public void getFaqs() throws Exception {

        mockMvc.perform(get("/api/board/notice")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-faq"))
                .andDo(MockMvcResultHandlers.print());
    }
}
