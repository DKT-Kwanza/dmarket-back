package com.dmarket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartCountResDto {
    private Integer cartCount;

    public CartCountResDto(Long cartCount){
        this.cartCount = cartCount.intValue();
    }
}
