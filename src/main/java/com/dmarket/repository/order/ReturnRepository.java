package com.dmarket.repository.order;

import com.dmarket.constant.OrderDetailState;
import com.dmarket.constant.ReturnState;
import com.dmarket.domain.order.Return;
import com.dmarket.dto.common.ReturnDto;
import com.dmarket.dto.response.ReturnResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnRepository extends JpaRepository<Return, Long> {

        @Query("SELECT new com.dmarket.dto.common.ReturnDto(r, o, p, pi, po) " +
                        "FROM Return r " +
                        "JOIN OrderDetail od ON od.orderDetailId = r.orderDetailId " +
                        "JOIN Order o ON o.orderId = od.orderId " +
                        "JOIN Product p ON p.productId = od.productId " +
                        "JOIN ProductOption po ON po.optionId = od.optionId " +
                        "JOIN ProductImgs pi ON pi.productId = p.productId " +
                        "WHERE r.returnState = :returnState AND pi.imgId = (" +
                        "SELECT MIN(pi2.imgId) FROM ProductImgs pi2 WHERE pi2.productId = od.productId" +
                        ") ORDER BY r.returnId DESC")
        Page<ReturnDto> getReturnsByReturnState(@Param("returnState") ReturnState returnState, Pageable pageable);

        @Query("SELECT new com.dmarket.dto.response.ReturnResDto$ReturnListResDto(" +
                        "(SELECT count(r) FROM Return r WHERE r.returnState = 'RETURN_REQUEST'), " +
                        "(SELECT count(r) FROM Return r WHERE r.returnState = 'COLLECT_ING'), " +
                        "(SELECT count(r) FROM Return r WHERE r.returnState = 'COLLECT_COMPLETE'))")
        ReturnResDto.ReturnListResDto getReturnsCount();

        @Modifying
        @Query("UPDATE Return r SET r.returnState = :state " +
                        "WHERE r.returnId = :returnId")
        void updateReturnStateByReturnId(@Param("returnId") Long returnId, @Param("state") ReturnState state);
}
