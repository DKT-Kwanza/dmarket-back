package com.dmarket.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReqDto {

    @NotNull
    private Long userId;

    @NotNull
    private Long optionId;

    @NotNull
    private Integer reviewRating;

    @NotNull
    private String reviewContents;

    private String reviewImg;
}