package com.dmarket.integrationTest;

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

import com.dmarket.TestUtility;

import com.dmarket.domain.user.User;
import com.dmarket.dto.request.CartReqDto;
import com.dmarket.dto.request.FaqReqDto;
import com.dmarket.dto.request.InquiryReqDto;
import com.dmarket.dto.request.MileageReqDto;
import com.dmarket.dto.request.NoticeReqDto;
import com.dmarket.dto.request.UserReqDto;
import com.dmarket.dto.request.UserReqDto.Emails;
import com.dmarket.dto.request.WishListReqDto;
import com.dmarket.jwt.JWTUtil;
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
public class UserIntegrationTest {

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
    @DisplayName("위시리스트에 담긴 상품인지 확인")
    public void checkIsWish() throws Exception {
        Long userId = 1L;
        Long productId = 1L;

        mockMvc.perform(get("/api/users/" + userId + "/wish/" + productId)
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("check-is-wish"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("위시리스트 조회")
    public void getWishlistByUserId() throws Exception {
        Long userId = 9L;

        mockMvc.perform(get("/api/users/" + userId + "/wish")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-wishlist-by-user-id"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("장바구니 상품 개수 조회")
    public void getCartCount() throws Exception {
        Long userId = 9L;

        mockMvc.perform(get("/api/users/" + userId + "/cart-count")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-cart-count"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("서브헤더 사용자 정보 및 마일리지 조회")
    public void getSubHeader() throws Exception {
        Long userId = 9L;
        mockMvc.perform(get("/api/users/" + userId + "/mypage/mileage")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-sub-header"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("사용자 정보 조회")
    public void getUserInfoByUserId() throws Exception {
        Long userId = 9L;

        mockMvc.perform(get("/api/users/" + userId + "/mypage/myinfo")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-user-info-by-user-id"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("장바구니 상품 개수 조회")
    public void getCarts() throws Exception {
        Long userId = 9L;

        mockMvc.perform(get("/api/users/" + userId + "/cart")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-carts"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("작성한 Qna 조회")
    public void getQna() throws Exception {
        Long userId = 9L;

        mockMvc.perform(get("/api/users/" + userId + "/mypage/qna")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-qna"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("리뷰 작성 가능한 상품 목록 조회")
    public void getAvailableReviews() throws Exception {
        Long userId = 9L;

        mockMvc.perform(get("/api/users/" + userId + "/mypage/available-reviews")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-available-reviews"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("작성한 리뷰 목록 조회")
    public void getWrittenReviews() throws Exception {
        Long userId = 9L;

        mockMvc.perform(get("/api/users/" + userId + "/mypage/written-reviews")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-written-reviews"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("마일리지 사용 내역 조회")
    public void getMileageUsage() throws Exception {
        Long userId = 9L;

        mockMvc.perform(get("/api/users/" + userId + "/mypage/mileage-usage")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-mileage-usage"))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    @DisplayName("작성한 고객 문의 목록 조회")
    public void getUserInquiryAllByUserId() throws Exception {
        Long userId = 9L;

        mockMvc.perform(get("/api/users/" + userId + "/mypage/inquiry")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-user-inquiry-all-by-user-id"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("사용자 주문 내역 상세 조회")
    public void getUserOrderDetailListByOrderId() throws Exception {
        Long userId = 9L;
        Long orderId = 3384L;
        mockMvc.perform(get("/api/users/" + userId + "/mypage/orders/" + orderId)
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-user-inquiry-all-by-user-id"))
                .andDo(MockMvcResultHandlers.print());
    }
    
    @Test
    @DisplayName("주문 목록 조회")
    public void getUserOrderList() throws Exception {
        Long userId = 9L;
        mockMvc.perform(get("/api/users/" + userId + "/mypage/orders")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("get-user-order-list"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("회원가입")
    public void join() throws Exception {
        UserReqDto.Join testDto = TestUtility.createTestJoinDto();
        mockMvc.perform(post("/api/users/join")
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(testDto))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("join"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("이메일 인증 코드 전송")
    public void email() throws Exception {
        String testEmail = "testEmail@gachon.ac.kr";
        mockMvc.perform(post("/api/users/email")
                .header("Authorization", token)
                .content(testEmail)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("email"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("이메일 인증 코드 확인")
    public void emailVerify() throws Exception {
        UserReqDto.Emails testDto = TestUtility.createTestEmailDto();
        mockMvc.perform(post("/api/users/email/verify")
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(testDto))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("email-verify"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("장바구니 추가")
    public void addCart() throws Exception {
        Long userId =9L;
        CartReqDto.AddCartReqDto testDto =  TestUtility.createTestAddCartReqDto();
        
        mockMvc.perform(post("/api/users/" + userId + "/cart")
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(testDto))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("add-cart"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("위시리스트 추가")
    public void addWish() throws Exception {
        Long userId =9L;
        WishListReqDto.AddWishReqDto testDto =  TestUtility.createTestAddWishReqDto();
        
        mockMvc.perform(post("/api/users/" + userId + "/wish")
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(testDto))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("add-wish"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("위시리스트 삭제")
    public void deleteWishlistId() throws Exception {
        Long userId =9L;
        Long wishlistId = 6L;
        
        mockMvc.perform(delete("/api/users/" + userId + "/wish/" + wishlistId)
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("delete-wishlist-id"))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    @DisplayName("비밀번호 변경")
    public void updatePassword() throws Exception {
        Long userId =9L;
        UserReqDto.ChangePwd testDto = TestUtility.createTestChangePwdDto();
        
        mockMvc.perform(put("/api/users/" + userId + "/mypage/change-pwd")
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(testDto))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("update-password"))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    @DisplayName("배송지 수정")
    public void updateAddress() throws Exception {
        Long userId =9L;
        UserReqDto.UserAddress testDto = TestUtility.createTestUserAddressDto();
        
        mockMvc.perform(put("/api/users/" + userId + "/mypage/myinfo")
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(testDto))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("update-password"))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    @DisplayName("장바구니 삭제")
    public void deleteCart() throws Exception {
        Long userId =9L;
        
        mockMvc.perform(delete("/api/users/" + userId + "/cart/6, 7, 8")
                .header("Authorization", token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("delete-cart"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("마일리지 충전 요청")
    public void mileageChargeReq() throws Exception {
        Long userId =9L;
        MileageReqDto.MileageChargeReqDto testDto =  TestUtility.createTestMileageChargeReqDto();
        
        mockMvc.perform(post("/api/users/" + userId + "/mypage/mileage-charge")
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(testDto))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcRestDocumentation.document("mileage-charge-req"))
                .andDo(MockMvcResultHandlers.print());
    }

}
