package com.dmarket.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wishlist {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishlistId;

    private Long userId;
    private Long productId;
}
