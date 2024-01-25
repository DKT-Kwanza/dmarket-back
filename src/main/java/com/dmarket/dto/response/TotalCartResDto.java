package com.dmarket.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import com.dmarket.dto.common.CartListDto;

@Data
@NoArgsConstructor
public class TotalCartResDto {
    private Integer cartCount;
    private Integer totalPay;
    private List<CartListDto> cartList;


    public TotalCartResDto(List<CartListDto> cartListDtos) {
        this.cartCount = cartListDtos.size();
        this.totalPay = cartListDtos.stream()
                .mapToInt(CartListDto::getProductTotalSalePrice)
                .sum();
        this.cartList = cartListDtos;
    }

}
