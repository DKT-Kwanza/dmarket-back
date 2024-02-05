package com.dmarket.dto.common;

import com.dmarket.constant.OrderDetailState;
import com.dmarket.domain.order.OrderDetail;
import com.dmarket.domain.product.Category;
import com.dmarket.domain.product.Product;
import com.dmarket.domain.product.ProductImgs;
import com.dmarket.domain.product.ProductOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ProductCommonDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductDetailListDto {
        private Long detailId;
        private Long proudctId;
        private String productBrand;
        private String productName;
        private String productImg;      //상품이미지 1개만
        private String productOption;   // 옵션명
        private Integer productCount;   // 주문 수량
        private Integer productTotalSalePrice;  // 개별상품 종합판매가 orderdetailsaleprice
        private OrderDetailState orderStauts;   // 주문 상태

        public ProductDetailListDto(OrderDetail orderDetail, Product product, ProductOption productOption, ProductImgs productImgs) {
            this.detailId = orderDetail.getOrderDetailId();
            this.proudctId = product.getProductId();
            this.productBrand = product.getProductBrand();
            this.productName = product.getProductName();
            this.productImg = productImgs.getImgAddress();
            this.productOption = productOption.getOptionName();
            this.productCount = orderDetail.getOrderDetailCount();
            this.productTotalSalePrice = orderDetail.getOrderDetailSalePrice();
            this.orderStauts = orderDetail.getOrderDetailState();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDto {
        private Long productId;
        private Float productRating;
        private Long productReviewCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor

    public static class ProductOptionDto {
        private Long optionId;
        private String optionName;
        private String optionValue;
        private Integer optionQuantity;
        private String optionStatus;

        public ProductOptionDto(ProductOption productOption) {
            this.optionId = productOption.getOptionId();
            this.optionName = productOption.getOptionName();
            this.optionValue = productOption.getOptionValue();
            this.optionQuantity = productOption.getOptionQuantity();
            this.optionStatus = productOption.getOptionQuantity() == 0 ? "품절" : "판매중";
        }

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductReviewDto {
        private Long reviewId;
        private String reviewWriter;
        private String productOption;
        private Integer reviewRating;
        private String reviewContents;
        private LocalDateTime reviewCreatedDate;
        private String reviewImg;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductListDto {
        private Long productId;
        private String productBrand;
        private String productName;
        private Integer productSalePrice;
        private String productCategory;
        private Long optionId;
        private String optionName;
        private String optionValue;
        private Integer optionQuantity;
        private LocalDateTime productRegistDate;
        private List<String> imgList;

        public ProductListDto(Product product, Category category, ProductOption productOption, List<String> imgs) {
            this.productId = product.getProductId();
            this.productBrand = product.getProductBrand();
            this.productName = product.getProductName();
            this.productSalePrice = product.getProductSalePrice();
            this.productCategory = category.getCategoryName();
            this.optionId = productOption!=null ? productOption.getOptionId() : null;
            this.optionName = productOption!=null ? productOption.getOptionName() : null;
            this.optionValue = productOption!=null ? productOption.getOptionValue() : null;
            this.optionQuantity = productOption!=null ? productOption.getOptionQuantity() : null;
            this.productRegistDate = product.getProductCreatedDate();
            this.imgList = imgs;
        }

    }
}
