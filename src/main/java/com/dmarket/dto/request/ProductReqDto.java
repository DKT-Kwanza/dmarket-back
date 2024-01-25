package com.dmarket.dto.request;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReqDto {
    @NotNull
    private Long productId;

    @NotEmpty
    private String productBrand;

    @NotEmpty
    private String productName;

    @NotEmpty
    private String categoryName;

    @NotNull
    private Integer productPrice;

    @NotNull
    private Integer productSalePrice;

    @NotEmpty
    private String productDes;

    private List<OptionReqDto> optionList;

    private List<@URL String> imgList; // URL 어노테이션으로 리스트 내의 각 String이 URL 형식인지 검증
}

