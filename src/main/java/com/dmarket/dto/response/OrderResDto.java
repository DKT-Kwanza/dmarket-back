package com.dmarket.dto.response;

import com.dmarket.constant.OrderDetailState;
import com.dmarket.domain.order.*;
import com.dmarket.domain.product.Product;
import com.dmarket.domain.product.ProductImgs;
import com.dmarket.domain.product.ProductOption;
import com.dmarket.domain.user.User;
import com.dmarket.dto.common.OrderCommonDto;
import com.dmarket.dto.common.ProductCommonDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@NoArgsConstructor
public class OrderResDto<T> {

    private LocalDateTime orderDate;
    private Long orderId;
    private List<T> orderDetailList; // 제네릭 리스트 사용

    // 제네릭 생성자
    public OrderResDto(Order order, List<T> details) {
        this.orderDate = order.getOrderDate();
        this.orderId = order.getOrderId();
        this.orderDetailList = details;
    }

    @Data
    @NoArgsConstructor
    public static class OrderCancelResDto {
        private Long productId;
        private Long orderId;
        private String productName;
        private String productBrand;
        private String productImg;
        private String optionValue;
        private String optionName;
        private LocalDateTime orderDate; // 일반적으로 LocalDateTime을 사용하지만, 문자열로 표현된 날짜/시간을 받기 위해 여기서는 String 타입으로 선언
        private Integer productCount;
        private String orderState;


        public OrderCancelResDto(Long productId, Long orderId, String productName, String productBrand, String productImg, String optionValue, String optionName, LocalDateTime orderDate, Integer productCount, OrderDetailState orderStatus) {
            this.productId = productId;
            this.orderId = orderId;
            this.productName = productName;
            this.productBrand = productBrand;
            this.productImg = productImg;
            this.optionValue = optionValue;
            this.optionName = optionName;
            this.orderDate = orderDate;
            this.productCount = productCount;
            this.orderState = orderStatus.getLabel();
        }
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderDetailListResDto {
        private Long orderId;
        private LocalDateTime orderDate;
        private Integer totalPay;           // orderTotalPay
        private String userName;
        private Integer userPostalCode;
        private String userAddress;
        private String userDetailedAddress;     // userAddressDetail
        private List<ProductCommonDto.ProductDetailListDto> productDetailList;

        public OrderDetailListResDto(Order order, User user, List<ProductCommonDto.ProductDetailListDto> productDetailList) {
            this.orderId = order.getOrderId();
            this.orderDate = order.getOrderDate();
            this.totalPay = order.getOrderTotalPay();
            this.userName = user.getUserName();
            this.userPostalCode = user.getUserPostalCode();
            this.userAddress = user.getUserAddress();
            this.userDetailedAddress = user.getUserAddressDetail();
            this.productDetailList = productDetailList;
        }
    }

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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderListResDto {
        private Long confPayCount;           //결제 완료 상태 개수
        private Long preShipCount;           // 배송 준비 중 상태 개수
        private Long inTransitCount;         // 배송 중 상태 개수
        private Long cmpltDilCount;          // 배송 완료 상태 개수
        private Long orderCancelCount;       // 주문 취소 상태 개수
        private Long returnCount;            // 반품 상태 개수 RETURN_REQUEST + RETURN_COMPLETE
        private List<OrderCommonDto.OrderListDto> orderList;   // 주문 리스트
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderListAdminResDto {
        // 배송 목록 조회

        private Long orderId;
        private LocalDateTime orderDate;
        private Long detailId;
        private Long productId;
        private Long optionId;
        private String optionName;
        private String optionValue;
        private String productBrand;
        private String productName;
        private String productImg;
        private Integer productCount;
        private String orderStatus;

        //    public Integer getOrderCount() {
        //        return getProductCount();
        //    }
    }


}

