package com.dmarket.repository.order;

import com.dmarket.domain.order.OrderDetail;
import com.dmarket.dto.response.OrderDetailResDto;
import com.dmarket.dto.response.ReviewResDto;

import org.springframework.data.jpa.repository.JpaRepository;
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
}
