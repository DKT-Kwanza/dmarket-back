package com.dmarket.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderReturnReqDto {

    @NotNull
    private Long orderDetailId;
    @NotNull
    private String returnContents;
}
