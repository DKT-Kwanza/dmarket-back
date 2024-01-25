package com.dmarket.dto.response;

import java.time.LocalDate;

import com.dmarket.domain.user.User;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
public class UserResDto {
    private Long userId;
    private String userName;
    private String userEmail;
    private String userRole;
    private Integer userDktNum;
    private LocalDate userJoinDate;

    public UserResDto(User user) {
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.userEmail = user.getUserEmail();
        this.userRole = user.getUserRole().name();
        this.userDktNum = user.getUserDktNum();
        this.userJoinDate = user.getUserJoinDate();
    }

    
}
