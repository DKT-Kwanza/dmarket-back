package com.dmarket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendProductResDto {
    private Long cateId;
    private Long productId;
    private String productBrand;
    private String productName;
    private Integer productSalePrice;
    private Float productRating;
    private Long productReviewCount;
}
