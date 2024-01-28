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
}
