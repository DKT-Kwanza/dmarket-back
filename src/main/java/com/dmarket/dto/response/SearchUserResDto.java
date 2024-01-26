package com.dmarket.dto.response;


import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchUserResDto {
    private Long userId;
    private String userName;
    private String userEmail;
    private Integer userDktNum;
    private String userRole; // Role 타입을 String으로 변환하여 저장할 것이므로 String 타입으로 선언합니다.
    private LocalDate userJoinDate;

}

