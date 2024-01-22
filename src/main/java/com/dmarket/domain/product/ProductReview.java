package com.dmarket.domain.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductReview {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    private Long optionId;
    private Long productId;
    private Long userId;
    private Float reviewRating;
    private String reviewContents;
    private String reviewImg;
    private LocalDateTime reviewCreatedDate;
}
