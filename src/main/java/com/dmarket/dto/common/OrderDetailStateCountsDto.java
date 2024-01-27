package com.dmarket.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailStateCountsDto {
    private Long orderCompleteCount;
    private Long deliveryReadyCount;
    private Long deliveryIngCount;
    private Long deliveryCompleteCount;
}
