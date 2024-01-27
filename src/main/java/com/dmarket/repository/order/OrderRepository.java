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

    Optional<Order> findByOrderId(Long orderId);

    @Query("SELECT new com.dmarket.dto.common.OrderDetailStateCountsDto(" +
            "count(od) FILTER (WHERE od.orderDetailState = 'ORDER_COMPLETE'), " +
            "count(od) FILTER (WHERE od.orderDetailState = 'DELIVERY_READY'), " +
            "count(od) FILTER (WHERE od.orderDetailState = 'DELIVERY_ING'), " +
            "count(od) FILTER (WHERE od.orderDetailState = 'DELIVERY_COMPLETE')) " +
            "FROM OrderDetail od")
    OrderDetailStateCountsDto getOrderDetailStateCounts();

}

