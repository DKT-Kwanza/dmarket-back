package com.dmarket.domain.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long optionId;

    @Column(nullable = false)
    private Long productId;

    private String optionName;

    private String optionValue;

    @Column(nullable = false)
    private Integer optionQuantity;

    @Builder
    public ProductOption(Long productId, String optionName, String optionValue, Integer optionQuantity) {
        this.productId = productId;
        this.optionName = optionName;
        this.optionValue = optionValue;
        this.optionQuantity = optionQuantity;
    }
}
