package com.dmarket.dto.response;

import com.dmarket.domain.product.Product;
import com.dmarket.dto.common.ProductOptionDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class ProductInfoResDto {
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
    private List<ProductOptionDto> optionList;
    private List<String> imgList;

    public ProductInfoResDto(Product product, String productCategory, Long reviewCount, Boolean isWish, List<ProductOptionDto> options, List<String> imgs){
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
