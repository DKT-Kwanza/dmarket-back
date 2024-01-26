package com.dmarket.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class InquiryDetailsDto {
    private Long inquiryId;
    private String inquiryTitle;
    private String inquiryContents;
    private String inquiryType;
    private boolean inquiryStatus;
    private String inquiryWriter;
    private String inquiryImg;
    private String inquiryCreateDate;
    private String inquiryReplyContents;
}
