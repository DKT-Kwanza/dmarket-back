package com.dmarket.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductListDto {
    private Long productId;
    private String productBrand;
    private String productName;
    private String productImg;
    private Integer productSalePrice;
    private Float productRating;
    private Long productReviewCount;
}