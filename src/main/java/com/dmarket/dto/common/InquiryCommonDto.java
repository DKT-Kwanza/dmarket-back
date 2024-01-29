package com.dmarket.dto.common;

import com.dmarket.constant.InquiryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

public class InquiryCommonDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class InquiryDetailsDto {

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

    @Data
    public static class InquiryRequestDto {

        //    private Long userId;
        private InquiryType inquiryType;
        private String inquiryTitle;
        private String inquiryContents;
        private String inquiryImg;
        //    private Boolean inquiryState;

    }
}
