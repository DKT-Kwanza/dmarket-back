package com.dmarket.dto.response;
import com.dmarket.domain.order.*;
import com.dmarket.domain.product.*;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Data
@Getter
@NoArgsConstructor
public class ReviewResDto {
    private Long orderDetailId;
    private String orderBrand;
    private String productName;
    private String productImg;
    private String productOption;
    private Integer productCount;
    private Integer productTotalSalePrice;
    private Long reviewId;
    private String reviewContents;
    private Integer reviewRating;
    private LocalDateTime reviewCreatedDate;
    private String reviewImg;

    public ReviewResDto(OrderDetail orderDetail, Product product, ProductImgs productImgs, ProductOption productOption, ProductReview productReview){
        this.orderDetailId = orderDetail.getOrderDetailId();
        this.orderBrand = product.getProductBrand();
        this.productName = product.getProductName();
        this.productImg = productImgs.getImgAddress();
        this.productOption = productOption.getOptionValue();
        this.productCount = orderDetail.getOrderDetailCount();
        this.productTotalSalePrice = orderDetail.getOrderDetailSalePrice();
        this.reviewId = productReview.getReviewId();
        this.reviewContents = productReview.getReviewContents();
        this.reviewRating = productReview.getReviewRating();
        this.reviewCreatedDate = productReview.getReviewCreatedDate();
        this.reviewImg = productReview.getReviewImg();
    }

}
