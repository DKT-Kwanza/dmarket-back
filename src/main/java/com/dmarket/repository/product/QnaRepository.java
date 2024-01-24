package com.dmarket.repository.product;

import com.dmarket.domain.product.Qna;
import com.dmarket.dto.response.QnaProductIdListResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface QnaRepository extends JpaRepository<Qna, Long> {
    @Query("select new com.dmarket.dto.response.QnaProductIdListResDto(" +
            "q.qnaId, q.qnaSecret, u.userName, q.qnaTitle, q.qnaContents, q.qnaCreatedDate, " +
            "q.qnaState, qr.qnaReplyDate, qr.qnaReplyContents) " +
            "from Qna q " +
            "join User u on u.userId = q.userId " +
            "left join QnaReply qr on qr.qnaId = q.qnaId " +
            "where q.productId = :productId " +
            "order by q.qnaCreatedDate desc")

    Page<QnaProductIdListResDto> findQnasByProductId(@Param("productId") Long productId, Pageable pageable);
}


