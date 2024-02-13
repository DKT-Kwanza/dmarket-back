package com.dmarket;

import com.dmarket.constant.MileageContents;
import com.dmarket.constant.MileageReqState;
import com.dmarket.constant.Role;
import com.dmarket.domain.board.Notice;
import com.dmarket.domain.user.MileageReq;
import com.dmarket.domain.user.User;
import com.dmarket.dto.common.MileageCommonDto;
import com.dmarket.dto.request.NoticeReqDto;
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

}