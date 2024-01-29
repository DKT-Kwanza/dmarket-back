package com.dmarket.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class InquiryReqDto {
    @Getter
    @AllArgsConstructor
    public static class InquiryReplyRequestDto {
        private String inquiryReplyContents;
    }
}
