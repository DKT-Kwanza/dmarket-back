package com.dmarket;

import com.dmarket.constant.MileageContents;
import com.dmarket.constant.MileageReqState;
import com.dmarket.constant.Role;
import com.dmarket.domain.user.MileageReq;
import com.dmarket.domain.user.User;
import com.dmarket.dto.common.MileageCommonDto;

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

    public static MileageCommonDto.MileageReqListDto createTestMileageReq() {
        MileageCommonDto.MileageReqListDto testMileageReq = MileageCommonDto.MileageReqListDto.builder()
        .mileageReqId(1L)
        .mileageReqDate(LocalDateTime.now())
        .userId(1L)
        .userName("testName")
        .userEmail("testEmail")
        .mileageReqReason(MileageContents.PURCHASE)
        .mileageReqAmount(100000)
        .mileageReqState(MileageReqState.PROCESSING)
        .build();
        return testMileageReq;

    }

}