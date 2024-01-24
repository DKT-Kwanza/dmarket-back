package com.dmarket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoResDto {
    private String userName;
    private String userEmail;
    private Integer userDktNum;
    private String userPhoneNum;
    private String userAddress;
    private String userDetailAddress;
    private Integer userPostalCode;
    private LocalDate userJoinDate;
}
