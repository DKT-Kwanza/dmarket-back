package com.dmarket.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class InquiryReqDto {

    @Getter
    @AllArgsConstructor
    public static class InquiryReplyRequestDto {
        private String inquiryReplyContents;
    }
}
