package com.dmarket.domain.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private Long categoryId;

    @Column(nullable = false)
    private String productBrand;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer productPrice;

    @Column(nullable = false)
    private Integer productSalePrice;

    @Column(nullable = false, columnDefinition="TEXT")
    private String productDescription;

    @Column(nullable = true)
    private Float productRating;

    @Column(nullable = true)
    private LocalDateTime productCreatedDate;


    @Builder
    public Product(Long categoryId, String productBrand, String productName, Integer productPrice, Integer productSalePrice, String productDescription, Float productRating) {
        this.categoryId = categoryId;
        this.productBrand = productBrand;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productSalePrice = productSalePrice;
        this.productDescription = productDescription;
        this.productRating = productRating;
        this.productCreatedDate = LocalDateTime.now().withNano(0);
    }
}
