package com.dmarket.dto.request;

import lombok.Getter;
import lombok.Setter;

public class InquiryReqDto {
    @Getter
    @Setter
    public static class InquiryReplyRequestDto {
        private String inquiryReplyContents;
    }
}
