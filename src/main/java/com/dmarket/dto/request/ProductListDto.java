package com.dmarket.dto.request;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductListDto {
    private String brand;
    private String productName;
    private String categoryName;
    private String productPrice;
    private String productDes;
    private String productSalePrice;
    private List<String> imgList;
    private List<ProductListDto.Option> optionList;

    // 생성자, getter, setter 등 필요한 메서드들을 추가로 구현합니다.

    // Option 클래스를 내부에 정의합니다.
    @Getter
    @Setter
    public static class Option {
        private String optionName;
        private String optionValue;
        private int optionQuantity;

        // 생성자, getter, setter 등 필요한 메서드들을 추가로 구현합니다.
    }
}