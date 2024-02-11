package com.dmarket.dto.common;

import lombok.*;

public class UserCommonDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @ToString
    public static class TokenResponseDto {
        private String accesstoken;
        private String refreshtoken;
        private Long userId;
        private String role;
    }
}
