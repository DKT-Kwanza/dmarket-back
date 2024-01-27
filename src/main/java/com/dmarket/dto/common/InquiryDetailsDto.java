package com.dmarket.dto.common;

import com.dmarket.constant.InquiryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class InquiryDetailsDto {
    private Long inquiryId;
    private String inquiryTitle;
    private String inquiryContents;
    private InquiryType inquiryType;
    private Boolean inquiryStatus;
    private String inquiryWriter;
    private String inquiryImg;
    private String inquiryCreateDate;
    private String inquiryReplyContents;

    public InquiryDetailsDto(Long inquiryId, String inquiryTitle, String inquiryContents, InquiryType inquiryType,
                             Boolean inquiryStatus, String userName, String inquiryImg, LocalDateTime inquiryCreateDate,
                             String inquiryReplyContents) {
        this.inquiryId = inquiryId;
        this.inquiryTitle = inquiryTitle;
        this.inquiryContents = inquiryContents;
        this.inquiryType = inquiryType;
        this.inquiryStatus = inquiryStatus;
        this.inquiryWriter = userName;
        this.inquiryImg = inquiryImg;
        this.inquiryCreateDate = inquiryCreateDate.toString();
        this.inquiryReplyContents = inquiryReplyContents;
    }
}

