package com.dmarket.dto.response;

import com.dmarket.domain.product.Category;
import com.dmarket.domain.product.Product;
import com.dmarket.dto.common.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class ProductResDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductListResDto {
        private Long productId;
        private String productBrand;
        private String productName;
        private String productImg;
        private Integer productSalePrice;
        private Float productRating;
        private Long productReviewCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductListAdminResDto {
        private Long productId;
        private String productBrand;
        private String productName;
        private Integer productSalePrice;
        private String productCategory;
        private List<ProductCommonDto.ProductOptionListDto> optionList;
        private LocalDateTime productRegistDate;
        private List<String> imgList;

        public ProductListAdminResDto(Product product, Category category, List<ProductCommonDto.ProductOptionListDto> options, List<String> imgs) {
            this.productId = product.getProductId();
            this.productBrand = product.getProductBrand();
            this.productName = product.getProductName();
            this.productSalePrice = product.getProductSalePrice();
            this.productCategory = category.getCategoryName();
            this.optionList = options;
            this.productRegistDate = product.getProductCreatedDate();
            this.imgList = imgs;
        }

    }

    @Data
    @NoArgsConstructor
    public static class ProductInfoResDto {
        private Long productId;
        private String productCategory;
        private String productBrand;
        private String productName;
        private String productDes;
        private Integer productPrice;
        private Integer productSalePrice;
        private Float productRating;
        private Long productReviewCount;
        private Boolean productIsWish;
        private List<ProductCommonDto.ProductOptionDto> optionList;
        private List<String> imgList;

        public ProductInfoResDto(Product product, String productCategory, Long reviewCount, Boolean isWish, List<ProductCommonDto.ProductOptionDto> options, List<String> imgs) {
            this.productId = product.getProductId();
            this.productCategory = productCategory;
            this.productBrand = product.getProductBrand();
            this.productName = product.getProductName();
            this.productDes = product.getProductDescription();
            this.productPrice = product.getProductPrice();
            this.productSalePrice = product.getProductPrice();
            this.productRating = product.getProductRating();
            this.productReviewCount = reviewCount;
            this.productIsWish = isWish;
            this.optionList = options;
            this.imgList = imgs;
        }
    }

    @Getter
    @Setter
    public static class ProductInfoOptionResDto {
        // 재고 추가 Res
        private Long productId;
        private String productBrand;
        private String productName;
        private Long optionId;
        private String optionValue;
        private String optionName;
        private String productImg;
        private Integer optionQuantity;

        @Builder
        public ProductInfoOptionResDto(Long productId, String productBrand, String productName,
                                       Long optionId, String optionValue, String optionName,
                                       String productImg, Integer optionQuantity) {
            this.productId = productId;
            this.productBrand = productBrand;
            this.productName = productName;
            this.optionId = optionId;
            this.optionValue = optionValue;
            this.optionName = optionName;
            this.productImg = productImg;
            this.optionQuantity = optionQuantity;
        }
    }

    @Data
    @NoArgsConstructor
    public static class ProductReviewListResDto {
        private Long productId;
        private Float productRating;
        private Long productReviewCount;
        private int totalPage;
        private List<ProductCommonDto.ProductReviewDto> reviewList;

        public ProductReviewListResDto(ProductCommonDto.ProductDto product, int totalPage, List<ProductCommonDto.ProductReviewDto> reviewList) {
            this.productId = product.getProductId();
            this.productRating = product.getProductRating();
            this.productReviewCount = product.getProductReviewCount();
            this.totalPage = totalPage;
            this.reviewList = reviewList;
        }
    }

    @Data
    public static class ProductToOrderRespDto {
        private String userName;
        private String userPhoneNum;
        private String userEmail;
        private Integer userPostalCode;
        private String userAddress;
        private String userDetailAddress;
        private Integer totalPrice;
        private Integer totalPay;
        private List<ProductToOrder> productList;

        @Data
        public static class ProductToOrder {
            private Long productId;
            private String productBrand;
            private String productName;
            private Long optionId;
            private String productOption;
            private Integer productCount;
            private String productImg;
            private Integer productTotalPrice;
            private Integer productTotalSalePrice;
        }

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewProductResDto {
        // 최신 상품 조회
        private Long productId;
        private String productBrand;
        private String productName;
        private String productImg;
        private Integer productSalePrice;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendProductResDto {
        private Long cateId;
        private Long productId;
        private String productBrand;
        private String productName;
        private Integer productSalePrice;
        private Float productRating;
        private Long productReviewCount;
    }
}
