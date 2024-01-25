package com.dmarket.dto.response;

import com.dmarket.constant.InquiryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InquiryListResDto {
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
        this.inquiryType = inquiryType; // Use the enum directly
        this.inquiryStatus = inquiryStatus;
        this.inquiryImg = inquiryImg;
        this.inquiryCreateDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(inquiryCreateDate);
        this.inquiryWriter = inquiryWriter;
    }
}
