package com.dmarket.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MileageReqDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MileageChargeReqDto {
        @NotNull
        private Integer mileageCharge;
    }
}
