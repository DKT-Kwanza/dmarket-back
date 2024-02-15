package com.dmarket.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class InquiryReqDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InquiryReplyRequestDto {
        private String inquiryReplyContents;
    }
}
