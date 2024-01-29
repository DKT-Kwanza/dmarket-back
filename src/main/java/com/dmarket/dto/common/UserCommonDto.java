package com.dmarket.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class UserCommonDto {
    @Getter
    @Setter
    @AllArgsConstructor
    public static class TokenResponseDto {
        private String accesstoken;
        private String refreshtoken;
        private Long userId;
    }
}
