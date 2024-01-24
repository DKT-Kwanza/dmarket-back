package com.dmarket.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOptionDto {
    private Long optionId;
    private String optionName;
    private String optionValue;
    private Integer optionQuantity;
}