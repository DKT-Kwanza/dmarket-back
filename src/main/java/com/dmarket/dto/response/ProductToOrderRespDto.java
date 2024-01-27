package com.dmarket.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ProductToOrderRespDto {
    private String userName;
    private String userPhoneNum;
    private String userEmail;
    private Integer userPostalCode;
    private String userAddress;
    private String userDetailAddress;
    private Integer totalPrice;
    private Integer totalPay;
    private List<ProductToOrder> productList;

    @Data
    public static class ProductToOrder {
        private Long productId;
        private String productBrand;
        private String productName;
        private Long optionId;
        private String productOption;
        private Integer productCount;
        private String productImg;
        private Integer productTotalPrice;
        private Integer productTotalSalePrice;
    }

}
