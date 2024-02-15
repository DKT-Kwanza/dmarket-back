package com.dmarket.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class WishListReqDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddWishReqDto {
        @NotNull
        private Long productId;
    }
}
