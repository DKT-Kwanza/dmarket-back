package com.dmarket.dto.common;

import lombok.Getter;
import lombok.Setter;

public class UserCommonDto {
    @Getter
    @Setter
    public static class TokenResponseDto {
        private String accesstoken;
        private String refreshtoken;
    }
}
