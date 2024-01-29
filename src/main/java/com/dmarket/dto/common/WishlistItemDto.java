package com.dmarket.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistItemDto {

    private Long productId;
    private Long wishId;
    private String productName;
    private String productBrand;
    private String productImg;
    private Integer productSalePrice;
}
