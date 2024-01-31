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
    private String orderBrand;
    private String productName;
    private String productImg;
    private String productOption;
    private Integer productCount;
    private Integer productTotalSalePrice;

    public OrderDetailResDto(OrderDetail orderDetail, Product product, ProductImgs productImgs, ProductOption productOption) {
        this.orderDetailId = orderDetail.getOrderDetailId();
        this.orderBrand = product.getProductBrand();
        this.productName = product.getProductName();
        this.productImg = productImgs.getImgAddress();
        this.productOption = productOption.getOptionValue();
        this.productCount = orderDetail.getOrderDetailCount();
        this.productTotalSalePrice = orderDetail.getOrderDetailSalePrice();
    }

}
