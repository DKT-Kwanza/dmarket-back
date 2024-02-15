package com.dmarket.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class OrderReqDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderStatusReqDto {

        @NotNull
        private String orderStatus;
    }

    @Data
    public static class OrderPaymentReqDto {

        private Long userId;
        private Integer orderTotalPrice;
        private Integer orderTotalPay;
        private OrderDetail[] orderDetailList;

        @Data
        public static class OrderDetail {
            private Long productId;
            private Long optionId;
            private Integer orderDetailCount;
            private Integer orderDetailPrice;
            private Integer orderDetailSalePrice;
        }
    }
}
