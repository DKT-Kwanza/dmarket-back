package com.dmarket.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dmarket.domain.order.Refund;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {

    @Modifying
    @Query("UPDATE Refund rf SET rf.refundState = true WHERE rf.returnId = :returnId")
    void updateRefundCompleteByReturnId(@Param("returnId") Long returnId);
}
