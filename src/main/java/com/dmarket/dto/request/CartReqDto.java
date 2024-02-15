package com.dmarket.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

public class CartReqDto {

    @Data
    @AllArgsConstructor
    public static class AddCartReqDto {

        @NotNull
        private Long productId;

        @NotNull
        private Long optionId;

        @NotNull
        private Integer productCount;
    }
}
