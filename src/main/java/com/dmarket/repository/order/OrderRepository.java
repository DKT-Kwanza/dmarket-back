package com.dmarket.repository.order;

import com.dmarket.domain.order.Order;
import com.dmarket.dto.common.OrderCommonDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.userId = :userId ORDER BY o.orderDate DESC")
    List<Order> findByUserIdOrderedByOrderIdDesc(@Param("userId") Long userId);

    Order findByOrderId(Long orderId);

    Page<Order> findByUserId(Pageable pageable,Long userId);

    @Query("select o from Order o left join OrderDetail od on o.orderId = od.orderId where od.orderDetailId = :orderDetailId")
    Order findByOrderDetailId(@Param("orderDetailId") Long orderDetailId);

    // 주문 총 결제 금액 변경
    @Modifying
    @Query("update Order o set o.orderTotalPay = o.orderTotalPay - :orderTotalPay, " +
            "o.orderTotalPrice = o.orderTotalPrice - :orderTotalPrice " +
            "where o.orderId = :orderId")
    void updateOrderTotalPrice(@Param("orderId") Long orderId, @Param("orderTotalPay") Integer orderTotalPay, @Param("orderTotalPrice") Integer orderTotalPrice);


    //배송 목록 조회

    //Optional<Order> findByOrderId(Long orderId); // 민혁님꺼랑 겹쳐서 일단 optional 수정했어요
//    @Query("SELECT new com.dmarket.dto.common.OrderDetailStateCountsDto(" +
//            "count(od) FILTER (WHERE od.orderDetailState = 'ORDER_COMPLETE'), " +
//            "count(od) FILTER (WHERE od.orderDetailState = 'DELIVERY_READY'), " +
//            "count(od) FILTER (WHERE od.orderDetailState = 'DELIVERY_ING'), " +
//            "count(od) FILTER (WHERE od.orderDetailState = 'DELIVERY_COMPLETE'), " +
//            "count(od) FILTER (WHERE od.orderDetailState = 'ORDER_CANCEL'), " +
//            "count(od) FILTER (WHERE od.orderDetailState = 'RETURN_REQUEST'), " +
//            "count(od) FILTER (WHERE od.orderDetailState = 'RETURN_COMPLETE')) " +
//            "FROM OrderDetail od")
//    OrderDetailStateCountsDto getOrderDetailStateCounts();
    // --- 배송 목록 조회 ---

    @Query("SELECT new com.dmarket.dto.common.OrderCommonDto$OrderDetailStateCountsDto(" +
            "count(od) FILTER (WHERE od.orderDetailState = 'ORDER_COMPLETE'), " +
            "count(od) FILTER (WHERE od.orderDetailState = 'DELIVERY_READY'), " +
            "count(od) FILTER (WHERE od.orderDetailState = 'DELIVERY_ING'), " +
            "count(od) FILTER (WHERE od.orderDetailState = 'DELIVERY_COMPLETE'), " +
            "count(od) FILTER (WHERE od.orderDetailState = 'ORDER_CANCEL'), " +
            "count(od) FILTER (WHERE od.orderDetailState = 'RETURN_REQUEST'), " +
            "count(od) FILTER (WHERE od.orderDetailState = 'RETURN_COMPLETE')) " +
            "FROM OrderDetail od")
    OrderCommonDto.OrderDetailStateCountsDto getOrderDetailStateCounts();
}

