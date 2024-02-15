package com.dmarket.integrationTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import com.dmarket.TestUtility;
import com.dmarket.controller.AdminController;
import com.dmarket.controller.ProductController;
import com.dmarket.domain.user.User;

import com.dmarket.dto.request.FaqReqDto;
import com.dmarket.dto.request.InquiryReqDto;
import com.dmarket.dto.request.NoticeReqDto;
import com.dmarket.dto.request.UserReqDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@Transactional
public class ProductIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    public void setUp() {
        token = "Bearer " + createTestToken();
    }

    private String createTestToken() {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 60 * 60 * 1000); // 1시간 후 만료

        return JWT.create()
                .withIssuer("testIssuer")
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .withClaim("userId", 9)
                .withClaim("role", "ROLE_USER")
                .withClaim("email", "testEmail@gachon.ac.kr")
                .sign(Algorithm.HMAC512("testSecret"));
    }

    @Test
    @DisplayName("카테고리 전체 목록 조회")
    public void getCategories() throws Exception {
        mockMvc.perform(get("/api/products/categories")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-categories"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("카테고리별 상품 목록 조건 조회")
    public void getCategoryProducts() throws Exception {
        Long cateId = 2L;
        mockMvc.perform(get("/api/products/categories/" + cateId)
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-category-products"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("카테고리별 상품 목록 조건 조회")
    public void getSearchProducts() throws Exception {
        mockMvc.perform(get("/api/products/search")
                .header("Authorization", token)
                .param("q", "나이키")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-search-products"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("최신 상품 조회")
    public void getLatestProducts() throws Exception {
        mockMvc.perform(get("/api/products/new-products")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-latest-products"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("전체 카테고리 할인율 높은 순으로 조회")
    public void getHighDiscountRateProducts() throws Exception {
        Long cateId = 2L;
        mockMvc.perform(get("/api/products/high-discount-rate/" + cateId)
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-high-discount-rate-products"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("상품 상세 조회")
    public void getProductInfo() throws Exception {
        Long productId = 2L;
        mockMvc.perform(get("/api/products/" + productId)
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-product-info"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("추천 상품 조회")
    public void recommendProduct() throws Exception {
        Long productId = 2L;
        mockMvc.perform(get("/api/products/" + productId + "/recommend")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("recommend-product"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("상품별 Qna 리스트 조회")
    public void getAnasByProductId() throws Exception {
        Long productId = 2L;
        mockMvc.perform(get("/api/products/" + productId + "/qnaList")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-qnas-by-product-id"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("상품별 사용자 리뷰 조회")
    public void getProductReviews() throws Exception {
        Long productId = 2L;
        mockMvc.perform(get("/api/products/" + productId + "/reviews")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-product-reviews"))
                .andDo(MockMvcResultHandlers.print());
    }
}
