package com.dmarket.dto.common;

import com.dmarket.domain.order.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class OrderCommonDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderDetailStateCountsDto {
        private Long orderCompleteCount;
        private Long deliveryReadyCount;
        private Long deliveryIngCount;
        private Long deliveryCompleteCount;
        private Long orderCancelCount;
        private Long returnRequestCount;
        private Long returnCompleteCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderListDto {
        private Long orderId;
        private LocalDateTime orderDate;
        private List<ProductCommonDto.ProductDetailListDto> productDetailList;

        public OrderListDto(Order order, List<ProductCommonDto.ProductDetailListDto> productDetailList) {
            this.orderId = order.getOrderId();
            this.orderDate = order.getOrderDate();
            this.productDetailList = productDetailList;
        }
    }
}
