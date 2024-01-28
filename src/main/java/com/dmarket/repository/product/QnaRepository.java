package com.dmarket.repository.product;

import com.dmarket.domain.product.Qna;
import com.dmarket.dto.common.QnaDto;
import com.dmarket.dto.response.QnaResDto.QnaDetailResDto;
import com.dmarket.dto.response.QnaResDto.QnaProductIdListResDto;
import com.dmarket.dto.response.QnaResDto;
import com.dmarket.dto.response.QnaResDto.QnaTotalListResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface QnaRepository extends JpaRepository<Qna, Long> {
    @Query("select new com.dmarket.dto.response.QnaResDto$QnaProductIdListResDto(" +
            "q.qnaId, q.qnaSecret, u.userName, q.qnaTitle, q.qnaContents, q.qnaCreatedDate, " +
            "q.qnaState, qr.qnaReplyDate, qr.qnaReplyContents) " +
            "from Qna q " +
            "join User u on u.userId = q.userId " +
            "left join QnaReply qr on qr.qnaId = q.qnaId " +
            "where q.productId = :productId " +
            "order by q.qnaCreatedDate desc")

    Page<QnaResDto.QnaProductIdListResDto> findQnasByProductId(@Param("productId") Long productId, Pageable pageable);


    @Query("SELECT new com.dmarket.dto.response.QnaResDto$QnaTotalListResDto(q, p, qr) " +
            "FROM Qna q " +
            "JOIN Product p ON p.productId = q.productId " +
            "LEFT JOIN QnaReply qr ON qr.qnaId = q.qnaId " +
            "WHERE q.userId = :userId ORDER BY q.qnaId DESC")
    Page<QnaResDto.QnaTotalListResDto> getQnasfindByUserId(@Param("userId") Long userId, Pageable pageable);

    // QnA 전체 조회, 페이징
    @Query("select new com.dmarket.dto.common.QnaDto(q.qnaId, p.productName, q.qnaTitle, u.userName, q.qnaCreatedDate, q.qnaState, q.qnaSecret) " +
            "from Qna q " +
            "join User u on q.userId = u.userId " +
            "join Product p on q.productId = p.productId")
    Page<QnaDto> findAllQna(Pageable pageable);

    // QnA 상세(개별)조회 + 답변 조회
    @Query("select new com.dmarket.dto.response.QnaResDto$QnaDetailResDto(q, p, u, qr) " +
            "from Qna q " +
            "join Product p on q.productId = p.productId " +
            "join User u on q.userId = u.userId " +
            "left join QnaReply qr on q.qnaId = qr.qnaId " +
            "where q.qnaId = :qnaId")
    QnaResDto.QnaDetailResDto findQnaAndReply(Long qnaId);
}


