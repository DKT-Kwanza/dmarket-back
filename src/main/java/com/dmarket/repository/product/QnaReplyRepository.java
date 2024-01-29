package com.dmarket.repository.product;

import com.dmarket.domain.product.QnaReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QnaReplyRepository extends JpaRepository<QnaReply, Long> {

    // qnaReplyId로 qnaId 불러오기
    @Query("select qr.qnaId from QnaReply qr where qr.qnaReplyId = :qnaReplyId")
    Long findQnaIdByQnaReplyId(Long qnaReplyId);
}
