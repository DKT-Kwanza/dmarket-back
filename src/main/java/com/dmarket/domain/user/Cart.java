package com.dmarket.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    private Long userId;
    private Long productId;
    private Long optionId;
    private Integer cartCount;

    @Builder
    public Cart(Long userId, Long productId, Long optionId, Integer cartCount){
        this.userId = userId;
        this.productId = productId;
        this.optionId = optionId;
        this.cartCount = cartCount;
    }
}
