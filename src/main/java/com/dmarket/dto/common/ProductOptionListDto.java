package com.dmarket.dto.common;

import com.dmarket.domain.product.ProductOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOptionListDto {
    private Long optionId;
    private String optionName;
    private String optionValue;
    private Integer optionQuantity;
    private String optionStatus;

    public ProductOptionListDto(ProductOption productOption){
        this.optionId = productOption.getOptionId();
        this.optionName = productOption.getOptionName();
        this.optionValue = productOption.getOptionValue();
        this.optionQuantity = productOption.getOptionQuantity();
        this.optionStatus = productOption.getOptionQuantity() == 0 ? "품절" : "판매중";}
}
