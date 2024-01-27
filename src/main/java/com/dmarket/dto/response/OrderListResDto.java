package com.dmarket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderListResDto {
    // 배송 목록 조회

    private Long orderId;
    private LocalDateTime orderDate;
    private Long detailId;
    private Long productId;
    private Long optionId;
    private String optionName;
    private String optionValue;
    private String productBrand;
    private String productName;
    private String productImg;
    private Integer productCount;
    private String orderStatus;

//    public Integer getOrderCount() {
//        return getProductCount();
//    }
}
