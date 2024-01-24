package com.dmarket.repository.product;

import com.dmarket.domain.product.Qna;
import com.dmarket.dto.response.QnaResDto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QnaRepository extends JpaRepository<Qna, Long> {

    @Query("SELECT new com.dmarket.dto.response.QnaResDto(q, p, qr) " +
            "FROM Qna q " +
            "JOIN Product p ON p.productId = q.productId " +
            "LEFT JOIN QnaReply qr ON qr.qnaId = q.qnaId " +
            "WHERE q.userId = :userId")
    List<QnaResDto> getQnasfindByUserId(@Param("userId") Long userId);
}
