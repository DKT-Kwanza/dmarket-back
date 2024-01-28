package com.dmarket.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancelReqDto {
    @NotNull
    private Long orderId;
    @NotNull
    private Long orderDetailId;
}
