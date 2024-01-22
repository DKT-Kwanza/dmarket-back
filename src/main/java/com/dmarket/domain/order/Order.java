package com.dmarket.domain.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private Long userId;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column(nullable = false)
    private Integer orderTotalPrice;

    @Column(nullable = false)
    private Integer orderTotalPay;
}
