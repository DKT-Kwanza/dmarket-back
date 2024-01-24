package com.dmarket.dto.response;

import lombok.*;

import java.time.LocalDate;

//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
@Data
@Builder
public class UserInfoResDto {
    private String userName;
    private String userEmail;
    private Integer userDktNum;
    private String userPhoneNum;
    private String userAddress;
    private String userAddressDetail;
    private Integer userPostalCode;
    private LocalDate userJoinDate;
}
