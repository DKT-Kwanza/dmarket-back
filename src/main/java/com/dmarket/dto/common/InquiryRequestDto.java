package com.dmarket.dto.common;

import com.dmarket.constant.InquiryType;
import lombok.Data;

@Data
public class InquiryRequestDto {

//    private Long userId;
    private InquiryType inquiryType;
    private String inquiryTitle;
    private String inquiryContents;
    private String inquiryImg;
//    private Boolean inquiryState;

}