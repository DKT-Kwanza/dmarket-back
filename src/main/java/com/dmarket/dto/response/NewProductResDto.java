package com.dmarket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewProductResDto {
    // 최신 상품 조회
    private Long productId;
    private String productBrand;
    private String productName;
    private String productImg;
    private Integer productSalePrice;
}