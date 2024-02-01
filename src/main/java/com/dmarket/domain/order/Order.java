package com.dmarket.domain.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private Long userId;

    @Column(nullable = false)
    private Integer orderTotalPrice;

    @Column(nullable = false)
    private Integer orderTotalPay;

    @Column(nullable = false)
    private LocalDateTime orderDate;


    @Builder
    public Order(Long userId, Integer orderTotalPrice, Integer orderTotalPay, LocalDateTime orderDate) {
        this.userId = userId;
        this.orderTotalPrice = orderTotalPrice;
        this.orderTotalPay = orderTotalPay;
        this.orderDate = orderDate;
    }

    @Builder
    public Order(Long userId, Integer orderTotalPrice, Integer orderTotalPay) {
        this.userId = userId;
        this.orderTotalPrice = orderTotalPrice;
        this.orderTotalPay = orderTotalPay;
        this.orderDate = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }
}
