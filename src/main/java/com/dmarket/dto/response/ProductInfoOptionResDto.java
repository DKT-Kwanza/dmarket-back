package com.dmarket.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductInfoOptionResDto {
    // 재고 추가 Res
    private Long productId;
    private String productBrand;
    private String productName;
    private Long optionId;
    private String optionValue;
    private String optionName;
    private String productImg;
    private Integer optionQuantity;

    @Builder
    public ProductInfoOptionResDto(Long productId, String productBrand, String productName,
                             Long optionId, String optionValue, String optionName,
                             String productImg, Integer optionQuantity) {
        this.productId = productId;
        this.productBrand = productBrand;
        this.productName = productName;
        this.optionId = optionId;
        this.optionValue = optionValue;
        this.optionName = optionName;
        this.productImg = productImg;
        this.optionQuantity = optionQuantity;
    }
}
