package com.dmarket.dto.response;

import com.dmarket.constant.InquiryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InquiryResDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InquiryListResDto {
        //문의 목록 조회(카테고리별)

        private Long inquiryId;
        private String inquiryTitle;
        private String inquiryContents;
        private InquiryType inquiryType;
        private Boolean inquiryStatus;
        private String inquiryImg;
        private String inquiryCreateDate;
        private String inquiryWriter;

        public InquiryListResDto(Long inquiryId, String inquiryTitle, String inquiryContents, InquiryType inquiryType,
                                 Boolean inquiryStatus, String inquiryImg, LocalDateTime inquiryCreateDate, String inquiryWriter) {
            this.inquiryId = inquiryId;
            this.inquiryTitle = inquiryTitle;
            this.inquiryContents = inquiryContents;
            this.inquiryType = inquiryType;
            this.inquiryStatus = inquiryStatus;
            this.inquiryImg = inquiryImg;
            this.inquiryCreateDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(inquiryCreateDate);
            this.inquiryWriter = inquiryWriter;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInquiryAllResDto {
        private Long inquiryId;
        private InquiryType inquiryType;
        private String inquiryTitle;
        private String inquiryContents;
        private String inquiryImg;
        private LocalDateTime inquiryCreatedDate;
        private Boolean inquiryStatus;
        private String inquiryReplyContents;
        private LocalDateTime inquiryReplyDate;

        public InquiryType getInquiryType() {
            return inquiryType;
        }

        public void setInquiryType(InquiryType inquiryType) {
            this.inquiryType = inquiryType;
        }
    }



    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InquiryDetailResDto {
        //문의 상세 조회

        private Long inquiryId;
        private String inquiryTitle;
        private String inquiryContents;
        private InquiryType inquiryType;
        private Boolean inquiryStatus;
        private String inquiryImg;
        private String inquiryCreateDate;
        private String inquiryWriter;
        private Long inquiryReplyId;
        private String inquiryReplyContents;
        private String inquiryReplyDate;

        public InquiryDetailResDto(Long inquiryId, String inquiryTitle, String inquiryContents, InquiryType inquiryType,
                                   Boolean inquiryStatus, String inquiryImg, LocalDateTime inquiryCreateDate, String inquiryWriter,
                                   Long inquiryReplyId, String inquiryReplyContents, LocalDateTime inquiryReplyDate) {
            this.inquiryId = inquiryId;
            this.inquiryTitle = inquiryTitle;
            this.inquiryContents = inquiryContents;
            this.inquiryType = inquiryType;
            this.inquiryStatus = inquiryStatus;
            this.inquiryImg = inquiryImg;
            this.inquiryCreateDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(inquiryCreateDate);
            this.inquiryWriter = inquiryWriter;
            this.inquiryReplyId = inquiryReplyId;
            this.inquiryReplyContents = inquiryReplyContents;
            // inquiryReplyDate가 null이 아닌 경우에만 날짜 형식으로 변환
            if (inquiryReplyDate != null) {
                this.inquiryReplyDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(inquiryReplyDate);
            } else {
                this.inquiryReplyDate = null;
            }
        }

    }

}
