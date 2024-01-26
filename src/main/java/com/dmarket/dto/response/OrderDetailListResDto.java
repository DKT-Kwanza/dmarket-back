package com.dmarket.dto.response;

import com.dmarket.domain.order.Order;
import com.dmarket.domain.user.User;
import com.dmarket.dto.common.ProductDetailListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailListResDto {
    private Long orderId;
    private LocalDateTime orderDate;
    private Integer totalPay;           // orderTotalPay
    private String userName;
    private Integer userPostalCode;
    private String userAddress;
    private String userDetailedAddress;     // userAddressDetail
    private List<ProductDetailListDto> productDetailList;

    public OrderDetailListResDto(Order order, User user,List<ProductDetailListDto> productDetailList){
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
