package com.dmarket.service;

import com.dmarket.domain.user.User;
import com.dmarket.dto.request.UserReqDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void AopCheck() {
        assertThat(AopUtils.isAopProxy(userService)).isTrue();
    }

    @Test
    @DisplayName("회원가입")
    void join() {

        //given
        UserReqDto.Join dto = UserReqDto.Join.builder()
                .userEmail("abc@gachon.ac.kr")
                .userPassword("qwe123!@#")
                .userDktNum(12345)
                .userName("홍길동")
                .userPhoneNum("010-1234-5678")
                .userJoinDate(LocalDate.now())
                .userPostalCode(12345)
                .userAddress("주소")
                .userDetailedAddress("상세주소")
                .build();

        //when
        Long userId = userService.join(dto);
        User findUser = userService.findUserById(userId);

        //then
        assertThat(findUser).isNotNull();
        assertThat(findUser.getUserId()).isEqualTo(userId);
        assertThat(findUser.getUserEmail()).isEqualTo(dto.getUserEmail());
        assertThat(findUser.getUserDktNum()).isEqualTo(dto.getUserDktNum());
        assertThat(findUser.getUserName()).isEqualTo(dto.getUserName());
        assertThat(findUser.getUserPhoneNum()).isEqualTo(dto.getUserPhoneNum());
        assertThat(findUser.getUserJoinDate()).isEqualTo(dto.getUserJoinDate());
        assertThat(findUser.getUserPostalCode()).isEqualTo(dto.getUserPostalCode());
        assertThat(findUser.getUserAddress()).isEqualTo(dto.getUserAddress());
        assertThat(findUser.getUserAddressDetail()).isEqualTo(dto.getUserDetailedAddress());
        //비밀번호는 달라야 함
        assertThat(findUser.getUserPassword()).isNotEqualTo(passwordEncoder.encode(dto.getUserPassword()));
    }

    @Test
    @DisplayName("회원가입 유효성 검사 - 이메일")
    void verifyJoinEmail() {
        //이메일은 gachon.ac.kr로 끝나야 함

        //given
        UserReqDto.Join dto = UserReqDto.Join.builder()
                .userPassword("qwe123!@#")
                .userDktNum(12345)
                .userName("홍길동")
                .userPhoneNum("010-1234-5678")
                .userJoinDate(LocalDate.now())
                .userPostalCode(12345)
                .userAddress("주소")
                .userDetailedAddress("상세주소")
                .build();

        //when
        String email = "abc@naver.com";
        dto.setUserEmail(email);

        //then
        assertThatThrownBy(() -> userService.verifyJoin(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("회원가입 유효성 검사 - 비밀번호")
    void verifyJoinPassword() {
        //비밀번호는 영문자, 숫자, 특수문자 모두 포함되어야 함

        //given
        UserReqDto.Join dto = UserReqDto.Join.builder()
                .userEmail("test@gachon.ac.kr")
                .userDktNum(23456)
                .userName("홍길동")
                .userPhoneNum("010-1234-5678")
                .userJoinDate(LocalDate.now())
                .userPostalCode(12345)
                .userAddress("주소")
                .userDetailedAddress("상세주소")
                .build();


        /**
         * ASCII Code
         * a = 97, z = 122
         * A = 65, Z = 90
         * ! = 33
         * @ = 64
         * # = 35
         * $ = 36
         * % = 37
         * ^ = 94
         */
        StringBuilder passwordWithAlphabet = new StringBuilder();
        StringBuilder passwordWithNum = new StringBuilder();
        StringBuilder passwordWithSpecialChar = new StringBuilder();

        //영문자 대문자
        for (int i = 0; i < 5; i++) {
            int randInt = new Random().nextInt(26) + 97;
            char randChar = (char) randInt;
            passwordWithAlphabet.append(randChar);
        }

        //영문자 소문자
        for (int i = 0; i < 5; i++) {
            int randInt = new Random().nextInt(26) + 65;
            char randChar = (char) randInt;
            passwordWithAlphabet.append(randChar);
        }

        //숫자
        for (int i = 0; i < 5; i++) {
            int randInt = new Random().nextInt(10) + 48;
            char randChar = (char) randInt;
            passwordWithNum.append(randChar);
        }

        //특수문자 ASCII Code
        int[] specialChar = {33, 64, 35, 36, 37, 94};

        //특수문자
        for (int i = 0; i < 3; i++) {
            int randInt = new Random().nextInt(specialChar.length);
            char randChar = (char) specialChar[randInt];
            passwordWithSpecialChar.append(randChar);
        }

        //when, then
        dto.setUserPassword(String.valueOf(passwordWithAlphabet));
        System.out.println("[test] 알파벳으로 이루어진 비밀번호 = " + dto.getUserPassword());
        assertThatThrownBy(() -> userService.verifyJoin(dto))
                .isInstanceOf(IllegalArgumentException.class);

        dto.setUserPassword(String.valueOf(passwordWithAlphabet)+passwordWithSpecialChar);
        System.out.println("[test] 알파벳, 특수문자로 이루어진 비밀번호 = " + dto.getUserPassword());
        assertThatThrownBy(() -> userService.verifyJoin(dto))
                .isInstanceOf(IllegalArgumentException.class);

        dto.setUserPassword(String.valueOf(passwordWithNum)+passwordWithSpecialChar);
        System.out.println("[test] 숫자, 특수문자 이루어진 비밀번호 = " + dto.getUserPassword());
        assertThatThrownBy(() -> userService.verifyJoin(dto))
                .isInstanceOf(IllegalArgumentException.class);

        dto.setUserPassword("한글비밀번호");
        System.out.println("[test] 한글로 이루어진 비밀번호 = " + dto.getUserPassword());
        assertThatThrownBy(() -> userService.verifyJoin(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }
}