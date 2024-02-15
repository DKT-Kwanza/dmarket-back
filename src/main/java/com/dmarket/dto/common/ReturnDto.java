package com.dmarket.dto.common;

import com.dmarket.domain.order.*;
import com.dmarket.domain.product.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Getter
@NoArgsConstructor
public class ReturnDto {

    private Long returnId;
    private LocalDateTime returnReqDate;
    private Long orderId;
    private LocalDateTime orderDate;
    private Long productId;
    private String productBrand;
    private String productName;
    private String productImg;
    private String optionName;
    private String optionValue;
    private Integer productCount;
    private String returnStatus;
    private String returnContents;

    public ReturnDto(Return returns, Order order, Product product, ProductImgs productImgs,
                     ProductOption productOption) {
        this.returnId = returns.getReturnId();
        this.returnReqDate = returns.getReturnRequestDate();
        this.orderId = order.getOrderId();
        this.orderDate = order.getOrderDate();
        this.productId = product.getProductId();
        this.productBrand = product.getProductBrand();
        this.productName = product.getProductName();
        this.productImg = productImgs.getImgAddress();
        this.optionName = productOption.getOptionName();
        this.optionValue = productOption.getOptionValue();
        this.productCount = productOption.getOptionQuantity();
        this.returnContents = returns.getReturnReason();
        switch (returns.getReturnState()) {
            case RETURN_REQUEST:
                this.returnStatus = "반품 요청";
                break;
            case COLLECT_ING:
                this.returnStatus = "수거중";
                break;
            case COLLECT_COMPLETE:
                this.returnStatus = "수거 완료";
                break;
            default:
                throw new IllegalArgumentException("Unknown return state: " + returns.getReturnState());
        }
    }
}
