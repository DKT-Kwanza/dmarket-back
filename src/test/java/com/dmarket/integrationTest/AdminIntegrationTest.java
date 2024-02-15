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
public class AdminIntegrationTest{

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
    @DisplayName("관리자 검색")
    public void getAdmins_withDktNum() throws Exception {
        User testUser = TestUtility.createTestUser();

        // 실제 요청을 보내어 검증
        mockMvc.perform(get("/api/admin/admin-users")
                .header("Authorization", token)
                .param("q", "1")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-admins-with-dktnum"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("전체 관리자 조회")
    public void getAdmins_withoutDktNum() throws Exception {

        mockMvc.perform(get("/api/admin/admin-users")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-admins-without-dktnum"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("권한 변경")
    public void changeRole() throws Exception {

        Long userId = 1L;
        UserReqDto.ChangeRole newRole = new UserReqDto.ChangeRole();
        newRole.setNewRole("ROLE_GM");
        mockMvc.perform(put("/api/admin/admin-users/" + userId)
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(newRole))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("change-role"))
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("사용자 조회")
    public void getUser() throws Exception {

        mockMvc.perform(get("/api/admin/admin-user")
                .header("Authorization", token)
                .param("q", "qwe@gachon.ac.kr")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-user"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("사용자 삭제")
    public void deleteUser() throws Exception {
        Long userId = 18L;

        mockMvc.perform(delete("/api/admin/users/" + userId)
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("delete-user"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("마일리지 충전 요청/처리 내역 조회")
    public void getMileageRequests() throws Exception {
        mockMvc.perform(get("/api/admin/users/mileage-history")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-mileage-requests"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("마일리지 요청 승인")
    public void approveMileageReq() throws Exception {

        Long mileageReqId = 11L;
        mockMvc.perform(put("/api/admin/users/mileage/approval/" + mileageReqId)
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("approve-mileage-req"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("마일리지 요청 거부")
    public void refusalMileageReq() throws Exception {
        Long mileageReqId = 12L;

        mockMvc.perform(put("/api/admin/users/mileage/refusal/" + mileageReqId)
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("refusal-mileage-req"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("공지사항 목록 조회")
    public void getNotices() throws Exception {

        mockMvc.perform(get("/api/admin/board/notices")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-notices"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("공지사항 작성")
    public void postNotice() throws Exception {
        NoticeReqDto testNoticeReqDto = TestUtility.createTestNoticeReqDto();

        mockMvc.perform(post("/api/admin/board/notice")
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(testNoticeReqDto))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("post-notice"))
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("문의 목록 조회")
    public void getInquiries() throws Exception {
        mockMvc.perform(get("/api/admin/board/inquiry")
                .header("Authorization", token)
                .param("type", "회원")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-inquiries"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("문의 내역 상세 조회")
    public void getInquiryDetail() throws Exception {
        Long inquiryId = 1L;
        mockMvc.perform(get("/api/admin/board/inquiry/"+ inquiryId)
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-inquiries-detail"))
                .andDo(MockMvcResultHandlers.print());
    }
    

    @Test
    @DisplayName("문의 삭제")
    public void deleteInquiry() throws Exception {
        Long inquiryId = 4L;
        mockMvc.perform(delete("/api/admin/board/inquiry/"+ inquiryId)
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("delete-inquiry"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("문의 답변 등록")
    public void postInquiryReply() throws Exception {
        InquiryReqDto.InquiryReplyRequestDto testDto = TestUtility.createTestInquiryReplyRequestDto();
        Long inquiryId = 4L;
        mockMvc.perform(post("/api/admin/board/inquiry/reply/"+ inquiryId)
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(testDto))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("post-inquiry-reply"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("문의 답변 삭제")
    public void deleteInquiryReply() throws Exception {
        Long inquiryReplyId = 2L;
        mockMvc.perform(delete("/api/admin/board/inquiry/reply/"+ inquiryReplyId)
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("delete-inquiry-reply"))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    @DisplayName("FAQ 조회")
    public void getFaqs() throws Exception {
        mockMvc.perform(get("/api/admin/board/faq")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-faqs-admin"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("FAQ 등록")
    public void postFaq() throws Exception {
        FaqReqDto testDto = TestUtility.createTestFaqReqDto();
        mockMvc.perform(post("/api/admin/board/faq")
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(testDto))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("post-faq"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("faq 삭제")
    public void deleteFaq() throws Exception {
        Long faqId = 2L;
        mockMvc.perform(delete("/api/admin/board/faq/"+ faqId)
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("delete-faq"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("상품 목록 조회")
    public void getProductsListAdmin() throws Exception {
        Long cateId = 2L;
        mockMvc.perform(get("/api/admin/products/categories/" + cateId)
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-products-list-admin"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("상품 목록 카테고리별 검색 조회")
    public void getSearchProductList() throws Exception {
        Long cateId = 2L;
        mockMvc.perform(get("/api/admin/products/categories/" + cateId + "/search")
                .header("Authorization", token)
                .param("q", "나이키")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-search-products-list-admin"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("상품 상세 정보 조회")
    public void getProductInfo() throws Exception {
        Long productId = 2L;
        mockMvc.perform(get("/api/admin/products/" + productId)
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-product-info-admin"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("배송 목록 조회")
    public void getOrdersByStatus() throws Exception {
        String status = "결제 완료";
        mockMvc.perform(get("/api/admin/orders")
                .header("Authorization", token)
                .param("status", status)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-orders-by-status"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("주문 취소 목록 조회")
    public void getCanceledOrder() throws Exception {
        mockMvc.perform(get("/api/admin/cancel-order-details")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-canceled-order"))
                .andDo(MockMvcResultHandlers.print());
    }

    
    @Test
    @DisplayName("상태별 반품 목록 조회")
    public void getReturnsList() throws Exception {
        String returnStatus = "수거중";
        mockMvc.perform(get("/api/admin/orders/returns")
                .header("Authorization", token)
                .param("status", returnStatus)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-returns-list"))
                .andDo(MockMvcResultHandlers.print());
    }
}
