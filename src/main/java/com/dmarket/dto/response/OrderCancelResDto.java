package com.dmarket.dto.response;

import com.dmarket.constant.OrderDetailState;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class OrderCancelResDto{
    private Long productId;
    private Long orderId;
    private String productName;
    private String productBrand;
    private String productImg;
    private String optionValue;
    private String optionName;
    private LocalDateTime orderDate; // 일반적으로 LocalDateTime을 사용하지만, 문자열로 표현된 날짜/시간을 받기 위해 여기서는 String 타입으로 선언
    private Integer productCount;
    private String orderState;


    public OrderCancelResDto(Long productId, Long orderId, String productName, String productBrand, String productImg, String optionValue, String optionName, LocalDateTime orderDate, Integer productCount, OrderDetailState orderStatus) {
        this.productId = productId;
        this.orderId = orderId;
        this.productName = productName;
        this.productBrand = productBrand;
        this.productImg = productImg;
        this.optionValue = optionValue;
        this.optionName = optionName;
        this.orderDate = orderDate;
        this.productCount = productCount;
        this.orderState = orderStatus.getLabel();
    }
}
