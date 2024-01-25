package com.dmarket.domain.user;

import com.dmarket.constant.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String userPassword;

    @Enumerated(EnumType.STRING)
    private Role userRole;

    @Column(nullable = false)
    private Integer userDktNum;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private Integer userMileage;

    @Column(nullable = false)
    private String userPhoneNum;

    private LocalDateTime userRegisterDate;  //가입일

    @Column(nullable = false)
    private LocalDate userJoinDate;  //입사일

    @Column(nullable = false)
    private Integer userPostalCode;

    @Column(nullable = false)
    private String userAddress;

    @Column(nullable = false)
    private String userAddressDetail;

    public void updatePassword(String password) {
        this.userPassword = password;
    }

    @Builder
    public User(String userEmail, Integer userDktNum, String password, String userName, LocalDate userJoinDate, String userPhoneNum, Integer userPostalCode, String userAddress, String userAddressDetail) {
        this.userEmail = userEmail;
        this.userPassword = password;

        this.userDktNum = userDktNum;
        this.userName = userName;
        this.userPhoneNum = userPhoneNum;
        this.userMileage = 0;  //초기 마일리지는 0원으로 설정
        this.userRole = Role.ROLE_USER;  //초기 Role은 USER로 설정

        this.userJoinDate = userJoinDate;
        this.userRegisterDate = LocalDateTime.now().withNano(0);

        this.userPostalCode = userPostalCode;
        this.userAddress = userAddress;
        this.userAddressDetail = userAddressDetail;
    }
}
