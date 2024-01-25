package com.dmarket.dto.response;

import com.dmarket.constant.InquiryType;
import com.dmarket.domain.board.Inquiry;
import com.dmarket.repository.board.InquiryRepository;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInquiryAllResDto {
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
