package com.dmarket.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ProductToOrderReqDto {
    private Long userId;
    private List<ProductToOrder> productList;

    @Data
    public static class ProductToOrder {
        private Long productId;
        private Long optionId;
        private Integer productCount;
    }
}
