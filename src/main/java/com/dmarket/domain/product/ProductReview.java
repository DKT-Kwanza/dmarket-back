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
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(nullable = false)
    private Long optionId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long orderDetailId;

    @Column(nullable = false)
    private Integer reviewRating;

    @Column(nullable = false)
    private String reviewContents;

    @Column(nullable = true)
    private String reviewImg;

    @Column(nullable = true)
    private LocalDateTime reviewCreatedDate;


    @Builder
    public ProductReview(Long optionId, Long orderDetailId, Long productId, Long userId, Integer reviewRating, String reviewContents, String reviewImg) {
        this.optionId = optionId;
        this.productId = productId;
        this.orderDetailId = orderDetailId;
        this.userId = userId;
        this.reviewRating = reviewRating;
        this.reviewContents = reviewContents;
        this.reviewImg = reviewImg;
        this.reviewCreatedDate = LocalDateTime.now().withNano(0);
    }
}
