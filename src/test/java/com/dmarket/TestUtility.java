package com.dmarket;

import com.dmarket.constant.MileageContents;
import com.dmarket.constant.MileageReqState;
import com.dmarket.constant.Role;
import com.dmarket.domain.board.Inquiry;
import com.dmarket.domain.board.Notice;
import com.dmarket.domain.user.MileageReq;
import com.dmarket.domain.user.User;
import com.dmarket.dto.common.MileageCommonDto;
import com.dmarket.dto.request.CartReqDto;
import com.dmarket.dto.request.FaqReqDto;
import com.dmarket.dto.request.InquiryReqDto;
import com.dmarket.dto.request.MileageReqDto;
import com.dmarket.dto.request.NoticeReqDto;
import com.dmarket.dto.request.ProductReqDto;
import com.dmarket.dto.request.UserReqDto;
import com.dmarket.dto.request.WishListReqDto;
import com.dmarket.dto.response.InquiryResDto;
import com.dmarket.dto.response.InquiryResDto.InquiryDetailResDto;
import com.dmarket.dto.response.NoticeResDto;

import java.time.*;

public class TestUtility {

    public static User createTestUser() {
        User testUser = User.builder()
                .userEmail("testEmail")
                .userDktNum(1)
                .userPassword("testPassword")
                .userName("testUser")
                .userJoinDate(LocalDate.now())
                .userPhoneNum("010-0000-0000")
                .userPostalCode(12345)
                .userAddress("testAddress")
                .userAddressDetail("testAddressDetail")
                .build();
        testUser.setUserId(1L);
        return testUser;

    }

    public static User createTestGM() {
        User testUser = User.builder()
                .userEmail("testEmail")
                .userDktNum(2)
                .userPassword("testPassword")
                .userName("testGM")
                .userJoinDate(LocalDate.now())
                .userPhoneNum("010-0000-0000")
                .userPostalCode(12345)
                .userAddress("testAddress")
                .userAddressDetail("testAddressDetail")
                .build();
        testUser.setUserId(2L);
        testUser.setUserRole(Role.ROLE_GM);
        return testUser;
    }

    public static User createTestPM() {
        User testUser = User.builder()
                .userEmail("testEmail")
                .userDktNum(3)
                .userPassword("testPassword")
                .userName("testPM")
                .userJoinDate(LocalDate.now())
                .userPhoneNum("010-0000-0000")
                .userPostalCode(12345)
                .userAddress("testAddress")
                .userAddressDetail("testAddressDetail")
                .build();
        testUser.setUserId(3L);
        testUser.setUserRole(Role.ROLE_PM);
        return testUser;
    }

    public static User createTestSM() {
        User testUser = User.builder()
                .userEmail("testEmail")
                .userDktNum(4)
                .userPassword("testPassword")
                .userName("testPM")
                .userJoinDate(LocalDate.now())
                .userPhoneNum("010-0000-0000")
                .userPostalCode(12345)
                .userAddress("testAddress")
                .userAddressDetail("testAddressDetail")
                .build();
        testUser.setUserId(4L);
        testUser.setUserRole(Role.ROLE_SM);
        return testUser;
    }

    public static MileageCommonDto.MileageReqListDto createTestMileageReqDto() {
        MileageCommonDto.MileageReqListDto testMileageReqDto = MileageCommonDto.MileageReqListDto.builder()
                .mileageReqId(1L)
                .mileageReqDate(LocalDateTime.now())
                .userId(1L)
                .userName("testName")
                .userEmail("testEmail")
                .mileageReqReason(MileageContents.PURCHASE)
                .mileageReqAmount(100000)
                .mileageReqState(MileageReqState.PROCESSING)
                .build();
        return testMileageReqDto;
    }

    public static MileageReq createTestMileageReq() {
        MileageReq testMileageReq = MileageReq.builder()
                .userId(1L)
                .mileageReqReason(MileageContents.PURCHASE)
                .mileageReqAmount(100000)
                .mileageReqState(MileageReqState.PROCESSING)
                .build();
        return testMileageReq;
    }

    public static NoticeResDto createTestNoticeResDto() {
        Notice notice = Notice.builder()
                .userId(1L)
                .noticeTitle("testTitle")
                .noticeContents("testContents")
                .build();
        NoticeResDto noticeResDto = new NoticeResDto(notice);
        return noticeResDto;
    }

    public static NoticeReqDto createTestNoticeReqDto() {
        NoticeReqDto noticeReqDto = new NoticeReqDto(1L, "testTitle", "testContents");
        return noticeReqDto;
    }

    public static Notice createTestNotice() {
        Notice notice = Notice.builder()
                .userId(1L)
                .noticeTitle("testTitle")
                .noticeContents("testContents")
                .build();

        return notice;
    }

    public static Inquiry createTestInquiry() {
        Inquiry inquiry = Inquiry.builder()
                .inquiryTitle("testTitle")
                .inquiryContents("testContent")
                .userId(1L)
                .build();
        return inquiry;
    }

    public static InquiryReqDto.InquiryReplyRequestDto createTestInquiryReplyRequestDto() {
        InquiryReqDto.InquiryReplyRequestDto dto = new InquiryReqDto.InquiryReplyRequestDto("testContents");
        return dto;
    }

    public static FaqReqDto createTestFaqReqDto() {
        FaqReqDto dto = new FaqReqDto("회원", "testTitle", "testContents");
        return dto;
    }

    public static UserReqDto.Join createTestJoinDto() {
        UserReqDto.Join dto = new UserReqDto.Join("testEmail2@gachon.ac.kr", "!!TestPassword11", 1353135, "testName",
                "010-1321-1940", LocalDate.now(), 13352, "testAddress", "testAddress2");
        return dto;
    }

    public static UserReqDto.Emails createTestEmailDto() {
        UserReqDto.Emails dto = new UserReqDto.Emails("testEmail@gachon.ac.kr", "223541");
        return dto;
    }
    public static CartReqDto.AddCartReqDto createTestAddCartReqDto() {
        CartReqDto.AddCartReqDto dto = new CartReqDto.AddCartReqDto(6L, 14L, 3);
        return dto;
    }
    
    public static WishListReqDto.AddWishReqDto createTestAddWishReqDto() {
        WishListReqDto.AddWishReqDto dto = new WishListReqDto.AddWishReqDto(6L);
        return dto;
    }

    public static UserReqDto.ChangePwd createTestChangePwdDto() {
        UserReqDto.ChangePwd dto = new UserReqDto.ChangePwd("!!Qa4253", "!!Qa124253");
        return dto;
    }
    public static UserReqDto.UserAddress createTestUserAddressDto() {
        UserReqDto.UserAddress dto = new UserReqDto.UserAddress(12345, "testAddress", "testAddress22");
        return dto;
    }

    public static MileageReqDto.MileageChargeReqDto createTestMileageChargeReqDto() {
        MileageReqDto.MileageChargeReqDto dto = new MileageReqDto.MileageChargeReqDto(1000000);
        return dto;
    }
} 