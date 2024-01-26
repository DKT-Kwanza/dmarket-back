package com.dmarket.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponseDto {
    private String accesstoken;
    private String refreshtoken;
}