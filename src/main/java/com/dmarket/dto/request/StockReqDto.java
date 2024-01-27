package com.dmarket.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockReqDto {
    //재고 추가 request body
    private Long productId;
    private Long optionId;
    private Integer addCount;
}