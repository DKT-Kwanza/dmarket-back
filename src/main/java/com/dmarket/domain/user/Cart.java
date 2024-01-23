package com.dmarket.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
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
}
