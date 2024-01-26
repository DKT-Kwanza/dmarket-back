package com.dmarket.dto.common;

import com.dmarket.domain.order.Order;
import com.dmarket.domain.product.Product;

import java.time.LocalDateTime;
import java.util.List;

public class OrderListDto {
    private Long orderId;
    private LocalDateTime orderDate;
    private List<ProductDetailListDto> productDetailList;

    public OrderListDto(Order order, List<ProductDetailListDto> productDetailList){
        this.orderId = order.getOrderId();
        this.orderDate = order.getOrderDate();
        this.productDetailList = productDetailList;
    }
}
