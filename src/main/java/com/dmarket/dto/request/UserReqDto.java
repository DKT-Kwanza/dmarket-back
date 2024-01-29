package com.dmarket.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

public class UserReqDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserAddress {
        private Integer userPostalCode;
        private String userAddress;
        private String userDetailedAddress;
    }

    @Data
    @Builder
    public static class Join {

        @NotBlank
        @Email
        private String userEmail;

        @NotBlank
        @Size(min = 8) // 최소 8자리
        private String userPassword;

        @NotNull
        private Integer userDktNum; // 사원번호

        @NotBlank
        private String userName; // 이름

        @NotBlank
        private String userPhoneNum; // 전화번호

        @NotNull
        private LocalDate userJoinDate; // 입사일

        private Integer userPostalCode; // 우편번호
        private String userAddress; // 주소
        private String userDetailedAddress; // 상세주소
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangePwd {
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min = 8, message = "비밀번호는 8자리 이상 가능합니다.")
        private String currentPassword;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min = 8, message = "비밀번호는 8자리 이상 가능합니다.")
        private String newPassword;
    }

    @Getter
    public static class ChangeRole {
        String newRole;
    }

    @Data
    public static class Emails {
        private String userEmail;
        private String code;
    }
}
