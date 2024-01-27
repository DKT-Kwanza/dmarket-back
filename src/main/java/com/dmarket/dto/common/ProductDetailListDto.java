package com.dmarket.dto.common;

import com.dmarket.constant.OrderDetailState;
import com.dmarket.domain.order.OrderDetail;
import com.dmarket.domain.product.Product;
import com.dmarket.domain.product.ProductImgs;
import com.dmarket.domain.product.ProductOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailListDto {
    private Long detailId;
    private Long proudctId;
    private String productBrand;
    private String productName;
    private String productImg;      //상품이미지 1개만
    private String productOption;   // 옵션명
    private Integer productCount;   // 주문 수량
    private Integer productTotalSalePrice;  // 개별상품 종합판매가 orderdetailsaleprice
    private OrderDetailState orderStauts;   // 주문 상태

    public ProductDetailListDto(OrderDetail orderDetail, Product product, ProductOption productOption, ProductImgs productImgs){
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
