성package com.dmarket.dto.response;

import com.dmarket.constant.FaqType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FaqListResDto {
    private Long faqId;
    private FaqType faqType;
    private String faqQuestion;
    private String faqAnswer;

}
