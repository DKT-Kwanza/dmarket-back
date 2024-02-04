package com.dmarket.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductReqDto {

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

    @Data
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductListDto {
        private String brand;
        private String productName;
        private String categoryName;
        private String productPrice;
        private String productDes;
        private String productSalePrice;
        private List<String> imgList;
        private List<Option> optionList;

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

    @Data
    public static class ProductToOrderReqDto {
        private Long userId;
        private List<ProductToOrder> productList;

        @Data
        public static class ProductToOrder {
            private Long productId;
            private Long optionId;
            private Integer productCount;
        }
    }

    @Getter
    @Setter
    public static class StockReqDto {
        //재고 추가 request body
        private Long productId;
        private Long optionId;
        private Integer addCount;
    }
}

