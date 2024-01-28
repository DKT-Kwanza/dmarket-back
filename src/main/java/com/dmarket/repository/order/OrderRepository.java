package com.dmarket.repository.order;

import com.dmarket.domain.order.Order;

import java.util.List;

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

    @Query("select o from Order o left join OrderDetail od on o.orderId = od.orderId where od.orderDetailId = :orderDetailId")
    Order findByOrderDetailId(@Param("orderDetailId") Long orderDetailId);
}

