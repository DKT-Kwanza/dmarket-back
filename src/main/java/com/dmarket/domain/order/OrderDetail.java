package com.dmarket.domain.order;

import com.dmarket.constant.OrderDetailState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
}
