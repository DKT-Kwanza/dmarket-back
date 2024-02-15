package com.dmarket.repository.order;

import com.dmarket.constant.OrderDetailState;
import com.dmarket.domain.order.OrderDetail;
import com.dmarket.dto.common.ProductCommonDto;
import com.dmarket.dto.response.ReviewResDto;
import com.dmarket.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Query("SELECT new com.dmarket.dto.response.OrderDetailResDto(od, p, pi, po) " +
            "FROM OrderDetail od " +
            "JOIN Product p ON p.productId = od.productId " +
            "LEFT JOIN ProductImgs pi ON pi.productId = p.productId " +
            "LEFT JOIN ProductOption po ON po.optionId = od.optionId " +
            "LEFT JOIN ProductReview pr ON pr.orderDetailId = od.orderDetailId " +
            "WHERE od.orderId = :orderId AND pr.reviewId IS NULL " +
            "AND od.orderDetailState = com.dmarket.constant.OrderDetailState.DELIVERY_COMPLETE AND pi.imgId = (" +
            "SELECT MIN(pi2.imgId) FROM ProductImgs pi2 WHERE pi2.productId = od.productId" +
            ")")
    List<OrderDetailResDto> findOrderDetailsWithoutReviewByOrder(@Param("orderId") Long orderId);

    @Query("SELECT new com.dmarket.dto.response.ReviewResDto(od, p, pi, po, pr) " +
            "FROM OrderDetail od " +
            "JOIN Product p ON p.productId = od.productId " +
            "LEFT JOIN ProductImgs pi ON pi.productId = p.productId " +
            "LEFT JOIN ProductOption po ON po.optionId = od.optionId " +
            "JOIN ProductReview pr ON pr.orderDetailId = od.orderDetailId " +
            "WHERE od.orderId = :orderId " +
            "AND od.orderDetailState = com.dmarket.constant.OrderDetailState.DELIVERY_COMPLETE AND pi.imgId = (" +
            "SELECT MIN(pi2.imgId) FROM ProductImgs pi2 WHERE pi2.productId = od.productId" +
            ")")
    List<ReviewResDto> findOrderDetailsWithReviewByOrder(@Param("orderId") Long orderId);

    @Modifying
    @Query("UPDATE OrderDetail od SET od.orderDetailState = :state WHERE od.orderDetailId = :id")
    void updateOrderDetailState(@Param("id") Long detailId, @Param("state") OrderDetailState orderDetailState);

    @Modifying
    @Query("UPDATE OrderDetail od SET od.orderDetailState = :state " +
            "WHERE od.orderDetailId IN (SELECT r.orderDetailId FROM Return r WHERE r.returnId = :returnId)")
    void updateReturnCompleteByReturnId(@Param("returnId") Long returnId, @Param("state") OrderDetailState state);

    @Query("SELECT od.orderDetailSalePrice FROM OrderDetail od " +
            "JOIN Return r ON r.orderDetailId = od.orderDetailId " +
            "WHERE r.returnId = :returnId")
    Integer getOrderDetailSalePriceFindByReturnId(@Param("returnId") Long returnId);

    // 주문 내역 상세 조회
    @Query(value = "select new com.dmarket.dto.common.ProductCommonDto$ProductDetailListDto(od, p, po, pi) " +
            "from OrderDetail od " +
            "join Product p on od.productId = p.productId " +
            "join ProductOption po on od.optionId = po.optionId " +
            "join ProductImgs pi on p.productId = pi.productId " +
            "where od.orderId = :orderId and pi.imgId = (select min(pi2.imgId) from ProductImgs pi2 where pi2.productId = od.productId)")
    List<ProductCommonDto.ProductDetailListDto> findOrderDetailByOrderId(@Param("orderId") Long orderId);

    @Query(value = "select new com.dmarket.dto.common.ProductCommonDto$ProductDetailListDto(od, p, po, pi) " +
            "from OrderDetail od " +
            "join Product p on od.productId = p.productId " +
            "join ProductOption po on od.optionId = po.optionId " +
            "join ProductImgs pi on p.productId = pi.productId " +
            "where od.orderId = :orderId and pi.imgId = (" +
            "select min(pi2.imgId) from ProductImgs pi2 where pi2.productId = od.productId" +
            ")" +
            "order by od.orderDetailId desc")
    List<ProductCommonDto.ProductDetailListDto> findPageableOrderDetailByOrderId(Pageable pageable,@Param("orderId") Long orderId);

    // count (orderDetailState) by userId
    @Query(value = "SELECT COUNT(*) " +
            "FROM OrderDetail od " +
            "JOIN Order o ON od.orderId = o.orderId " +
            "JOIN User u ON o.userId = u.userId " +
            "WHERE u.userId = :userId " +
            "and od.orderDetailState = :orderDetailState " +
            "GROUP BY od.orderDetailState")
    Long countOrderDetailByUserIdAndOrderDetailState(@Param("userId") Long userId, @Param("orderDetailState") OrderDetailState orderDetailState);

    @Query("SELECT new com.dmarket.dto.response.OrderResDto$OrderCancelResDto(" +
            "   prod.productId, " +
            "   ord.orderId, " +
            "   prod.productName, " +
            "   prod.productBrand, " +
            "   MIN(img.imgAddress), " +
            "   popt.optionValue, " +
            "   popt.optionName, " +
            "   ord.orderDate, " +
            "   od.orderDetailCount, " +
            "   od.orderDetailState ) " + // Enum 값 그대로 조회
            "FROM OrderDetail od " +
            "JOIN Order ord ON od.orderId = ord.orderId " +
            "JOIN Product prod ON od.productId = prod.productId " +
            "JOIN ProductOption popt ON od.optionId = popt.optionId " +
            "JOIN ProductImgs img ON prod.productId = img.productId " +
            "WHERE od.orderDetailState = :orderCancelState " +
            "GROUP BY prod.productId, ord.orderId, prod.productName, prod.productBrand, popt.optionValue, popt.optionName, ord.orderDate, od.orderDetailCount, od.orderDetailState")
    Page<OrderResDto.OrderCancelResDto> findOrderCancelResDtosByOrderDetailState(Pageable pageable,@Param("orderCancelState") OrderDetailState orderCancelState);


    // 시간업데이트 및 상태변경
    @Modifying
    @Query(value = "update OrderDetail od " +
            "set od.orderDetailState = :orderDetailState " +
            "where od.orderDetailId = :orderDetailId")
    void updateOrderDetailUpdateDateAndOrderDetailStateByOrderDetailId(@Param("orderDetailId") Long orderDetailId, @Param("orderDetailState") OrderDetailState orderDetailState);


    // 주문 상세 번호에 따른 상품 판매가격
    @Query(value = "select od.orderDetailSalePrice " +
            "from OrderDetail od " +
            "where od.orderDetailId = :orderDetailId")
    Integer orderDetailTotalSalePrice(@Param("orderDetailId") Long orderDetailId);

    // 주문 상세 번호에 따른 상품 정상가
    @Query(value = "select od.orderDetailPrice " +
            "from OrderDetail od " +
            "where od.orderDetailId = :orderDetailId")
    Integer orderDetailTotalPrice(@Param("orderDetailId") Long orderDetailId);

    // null 값 처리를 위해 메서드 수정
    default Long safeCountOrderDetailByUserIdAndOrderDetailState(Long userId, OrderDetailState orderDetailState) {
        Long count = countOrderDetailByUserIdAndOrderDetailState(userId, orderDetailState);
        return count != null ? count : 0L;
    }


    //배송 목록 조회
    @Query("SELECT new com.dmarket.dto.response.OrderListAdminResDto(od.orderId, o.orderDate, od.orderDetailId, " +
            "od.productId, od.optionId, po.optionName, po.optionValue, p.productBrand, p.productName, pi.imgAddress, " +
            "od.orderDetailCount, od.orderDetailState) " +
            "FROM OrderDetail od " +
            "JOIN Order o ON od.orderId = o.orderId " +
            "JOIN Product p ON od.productId = p.productId " +
            "LEFT JOIN ProductOption po ON po.optionId = od.optionId " +
            "LEFT JOIN ProductImgs pi ON pi.productId = p.productId AND pi.imgId IN " +
            "  (SELECT min(pi2.imgId) FROM ProductImgs pi2 WHERE pi2.productId = p.productId) " +
            "WHERE od.orderDetailState = :status " +
            "ORDER BY o.orderDate DESC")
    Page<OrderListAdminResDto> findByStatus(@Param("status") OrderDetailState status, Pageable pageable);


    OrderDetail findByOrderDetailId(Long orderDetailId);

    //반품 상태 변경 userId검색을 위한 OrderDetailId검색
    @Query("SELECT od.orderId FROM OrderDetail od WHERE od.orderDetailId = :orderDetailId")
    Optional<Long> findOrderIdByOrderDetailId(@Param("orderDetailId") Long orderDetailId);

}