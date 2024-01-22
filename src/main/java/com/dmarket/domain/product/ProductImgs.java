package com.dmarket.domain.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImgs {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imgId;

    private Long productId;

    @Column(nullable = false, columnDefinition="TEXT")
    private String imgAddress;
}
