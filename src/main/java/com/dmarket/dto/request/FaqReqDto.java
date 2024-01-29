package com.dmarket.dto.request;

import com.dmarket.constant.FaqType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FaqReqDto {
    @NotNull
    private String faqType;
    @NotNull
    private String faqTitle;
    @NotNull
    private String faqContents;

}
