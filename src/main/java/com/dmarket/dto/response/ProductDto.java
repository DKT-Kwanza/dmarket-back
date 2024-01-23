package com.dmarket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long productId;
    private String productBrand;
    private String productName;
    private String productImg;
    private Integer productSalePrice;
    private Float productRating;
    private Long reviewCnt;
    // -> 혹시 이거 Long으로 그냥 반환해도 될지?

}