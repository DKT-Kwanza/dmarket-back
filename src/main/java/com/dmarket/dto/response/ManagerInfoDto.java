package com.dmarket.dto.response;

import com.dmarket.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManagerInfoDto {
    private Long userId;
    private String userName;
    private String userEmail;
    private String userRole; // Role Enum을 String으로 변환
    private String userJoinDate; // LocalDate를 String으로 변환

    public ManagerInfoDto(Long userId, String userName, String userEmail, Role userRole, LocalDateTime userJoinDate) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userRole = userRole.name();
        this.userJoinDate = userJoinDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Getters and Setters 생략
}
