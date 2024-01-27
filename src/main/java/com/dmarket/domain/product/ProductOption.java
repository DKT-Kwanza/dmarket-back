package com.dmarket.domain.product;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    // 재고 추가 SETTER
    public void setOptionQuantity(Integer optionQuantity) {
        this.optionQuantity = optionQuantity;
    }



}
