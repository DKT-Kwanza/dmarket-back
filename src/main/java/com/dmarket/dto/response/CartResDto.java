package com.dmarket.dto.response;

import com.dmarket.dto.common.CartCommonDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class CartResDto {
    @Data
    @NoArgsConstructor
    public static class TotalCartResDto {
        private Integer cartCount;
        private Integer totalPay;
        private List<CartCommonDto.CartListDto> cartList;


        public TotalCartResDto(List<CartCommonDto.CartListDto> cartListDtos) {
            this.cartCount = cartListDtos.size();
            this.totalPay = cartListDtos.stream()
                    .mapToInt(CartCommonDto.CartListDto::getProductTotalSalePrice)
                    .sum();
            this.cartList = cartListDtos;
        }

    }

    @Data
    @NoArgsConstructor
    public static class CartCountResDto {
        private Integer cartCount;

        public CartCountResDto(Long cartCount){
            this.cartCount = cartCount.intValue();
        }
    }
}
