package com.dmarket.dto.common;

import lombok.Data;

@Data
public class InquiryRequestDto {
    private String inquiryType;
    private String inquiryTitle;
    private String inquiryContents;
    private String inquiryImg;
}
