package com.dmarket.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCartReqDto {
    @NotNull
    private Long productId;

    @NotNull
    private Long optionId;

    @NotNull
    private Integer productCount;
}