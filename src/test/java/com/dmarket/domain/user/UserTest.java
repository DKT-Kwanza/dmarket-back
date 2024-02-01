package com.dmarket.domain.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

//@SpringBootTest
@Transactional
class UserTest {

    @Test
    void initMileage() {
        //given
        Random random = new Random();
        int year = LocalDate.now().getYear();
        int month = random.nextInt(12) + 1;
        int day = random.nextInt(27) + 1;
        LocalDate joinDate = LocalDate.of(year, month, day);

        User user = User.builder()
                .userEmail("test@gachon.com")
                .password("test123!@#")
                .userDktNum(131)
                .userName("홍길동")
                .userPhoneNum("010-1234-1234")
                .userJoinDate(joinDate)
                .userPostalCode(131)
                .userAddress("주소")
                .userAddressDetail("상세주소").build();

        Integer mileage = user.getUserMileage();
        int shouldBe = 0;

        //when
        if (month < 4) {
            //1분기 (1, 2, 3)
            shouldBe = 1200000;
        } else if (month < 7) {
            //2분기 (4, 5, 6)
            shouldBe = 900000;
        } else if (month < 10) {
            //3분기 (7, 8, 9)
            shouldBe = 600000;
        } else {
            //4분기 (10, 11, 12)
            shouldBe = 300000;
        }

        //then
        System.out.println("사용자 입사일 = " + user.getUserJoinDate());
        System.out.println("사용자 마일리지 = " + mileage);
        System.out.println("정책상 마일리지 = " + shouldBe);
        assertThat(mileage).isEqualTo(shouldBe);
    }
}