package com.dmarket.repository.order;

import com.dmarket.domain.order.Order;

import java.util.List;
import java.util.Optional;

import com.dmarket.dto.common.OrderDetailStateCountsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.userId = :userId ORDER BY o.orderId DESC")
    Page<Order> findByUserIdOrderedByOrderIdDesc(@Param("userId") Long userId, Pageable pageable);

    Order findByOrderId(Long orderId);

    List<Order> findByUserId(Long userId);

    //배송 목록 조회

    //Optional<Order> findByOrderId(Long orderId); // 민혁님꺼랑 겹쳐서 일단 optional 수정했어요
    @Query("SELECT new com.dmarket.dto.common.OrderDetailStateCountsDto(" +
            "count(od) FILTER (WHERE od.orderDetailState = 'ORDER_COMPLETE'), " +
            "count(od) FILTER (WHERE od.orderDetailState = 'DELIVERY_READY'), " +
            "count(od) FILTER (WHERE od.orderDetailState = 'DELIVERY_ING'), " +
            "count(od) FILTER (WHERE od.orderDetailState = 'DELIVERY_COMPLETE'), " +
            "count(od) FILTER (WHERE od.orderDetailState = 'ORDER_CANCEL'), " +
            "count(od) FILTER (WHERE od.orderDetailState = 'RETURN_REQUEST'), " +
            "count(od) FILTER (WHERE od.orderDetailState = 'RETURN_COMPLETE')) " +
            "FROM OrderDetail od")
    OrderDetailStateCountsDto getOrderDetailStateCounts();
    // --- 배송 목록 조회 ---
}

