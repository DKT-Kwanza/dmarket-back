package com.dmarket.dto.request;

import lombok.Data;

@Data
public class EmailReqDto {
    private String userEmail;
    private String code;
}
