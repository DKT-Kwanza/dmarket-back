package com.dmarket.dto.response;

import com.dmarket.domain.order.*;
import com.dmarket.domain.product.*;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
public class OrderDetailResDto {
    private Long orderDetailId;
    private String productBrand;
    private String productName;
    private String productImg;
    private String productOption;
    private Integer productCount;
    private Integer productTotalSalePrice;
    private Long optionId;
    private Long productId;

    public OrderDetailResDto(OrderDetail orderDetail, Product product, ProductImgs productImgs, ProductOption productOption) {
        this.orderDetailId = orderDetail.getOrderDetailId();
        this.productBrand = product.getProductBrand();
        this.productName = product.getProductName();
        this.productImg = productImgs.getImgAddress();
        this.productOption = productOption!=null ? productOption.getOptionValue() : null;
        this.productCount = orderDetail.getOrderDetailCount();
        this.productTotalSalePrice = orderDetail.getOrderDetailSalePrice();
        this.optionId = productOption!=null ? productOption.getOptionId() : null;
        this.productId = product.getProductId();
    }

}
