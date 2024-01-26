package com.dmarket.repository.order;

import com.dmarket.constant.OrderDetailState;
import com.dmarket.domain.order.Order;
import com.dmarket.domain.order.OrderDetail;
import com.dmarket.dto.common.ProductDetailListDto;
import com.dmarket.dto.response.OrderDetailListResDto;
import com.dmarket.dto.response.OrderDetailResDto;
import com.dmarket.dto.response.ReviewResDto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
        @Query("SELECT new com.dmarket.dto.response.OrderDetailResDto(od, p, pi, po) " +
                        "FROM OrderDetail od " +
                        "JOIN Product p ON p.productId = od.productId " +
                        "JOIN ProductImgs pi ON pi.productId = p.productId " +
                        "JOIN ProductOption po ON po.optionId = od.optionId " +
                        "LEFT JOIN ProductReview pr ON pr.orderDetailId = od.orderDetailId " +
                        "WHERE od.orderId = :orderId AND pr.reviewId IS NULL AND pi.imgId = (" +
                        "SELECT MIN(pi2.imgId) FROM ProductImgs pi2 WHERE pi2.productId = od.productId" +
                        ")")
        List<OrderDetailResDto> findOrderDetailsWithoutReviewByOrder(@Param("orderId") Long orderId);

        @Query("SELECT new com.dmarket.dto.response.ReviewResDto(od, p, pi, po, pr) " +
                        "FROM OrderDetail od " +
                        "JOIN Product p ON p.productId = od.productId " +
                        "JOIN ProductImgs pi ON pi.productId = p.productId " +
                        "JOIN ProductOption po ON po.optionId = od.optionId " +
                        "JOIN ProductReview pr ON pr.orderDetailId = od.orderDetailId " +
                        "WHERE od.orderId = :orderId AND pi.imgId = (" +
                        "SELECT MIN(pi2.imgId) FROM ProductImgs pi2 WHERE pi2.productId = od.productId" +
                        ")")
        List<ReviewResDto> findOrderDetailsWithReviewByOrder(@Param("orderId") Long orderId);

        @Modifying
        @Query("UPDATE OrderDetail od SET od.orderDetailState = :state WHERE od.orderDetailId = :id")
        void updateOrderDetailState(@Param("id") Long detailId, @Param("state") OrderDetailState orderDetailState);

        // 주문 내역 상세 조회
        @Query(value = "select new com.dmarket.dto.common.ProductDetailListDto(od, p, po, pi) " +
                        "from OrderDetail od " +
                        "join Product p on od.productId = p.productId " +
                        "join ProductOption po on od.optionId = po.optionId " +
                        "join ProductImgs pi on p.productId = pi.productId " +
                        "where od.orderId = :orderId and pi.imgId = (" +
                        "select min(pi2.imgId) from ProductImgs pi2 where pi2.productId = od.productId" +
                        ")")
        List<ProductDetailListDto> findOrderDetailByOrderId(@Param("orderId") Long orderId);
}