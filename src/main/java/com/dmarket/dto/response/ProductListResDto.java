package com.dmarket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResDto {
    private Long productId;
    private String productBrand;
    private String productName;
    private String productImg;
    private Integer productSalePrice;
    private Float ratingAvg;
    private Integer reviewCnt;

    public ProductListResDto(ProductDto produts){
        this.productId = produts.getProductId();
        this.productBrand = produts.getProductBrand();
        this.productName = produts.getProductName();
        this.productImg = produts.getProductImg();
        this.productSalePrice = produts.getProductSalePrice();
        this.ratingAvg = produts.getProductRating();
        this.reviewCnt = produts.getReviewCnt().intValue();
    }
}

