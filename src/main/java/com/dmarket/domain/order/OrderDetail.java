package com.dmarket.domain.order;

import com.dmarket.constant.OrderDetailState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderDetail {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderDetailId;

    private Long orderId;
    private Long optionId;
    private Long productId;
    // private Long reviewId; // 24일부로 수정됐습니다

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderDetailState orderDetailState;

    @Column(nullable = false)
    private Integer orderDetailCount;

    private Integer orderDetailPrice;
    private Integer orderDetailSalePrice;
    private LocalDateTime orderDetailUpdatedDate;

    public void updateOrderDetailUpdateDate(){
        this.orderDetailUpdatedDate = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }


    @Builder
    public OrderDetail(Long orderId, Long optionId, Long productId, OrderDetailState orderDetailState, Integer orderDetailCount, Integer orderDetailPrice, Integer orderDetailSalePrice) {
        this.orderId = orderId;
        this.optionId = optionId;
        this.productId = productId;
        this.orderDetailState = orderDetailState;
        this.orderDetailCount = orderDetailCount;
        this.orderDetailPrice = orderDetailPrice;
        this.orderDetailSalePrice = orderDetailSalePrice;
    }
}
