package com.dmarket.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponseDto {
    private String accesstoken;
    private String refreshtoken;
}