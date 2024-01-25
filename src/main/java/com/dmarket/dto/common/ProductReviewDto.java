package com.dmarket.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewDto {
    private Long reviewId;
    private String reviewWriter;
    private String productOption;
    private Integer reviewRating;
    private String reviewContents;
    private LocalDateTime reviewCreatedDate;
    private String reviewImg;
}
