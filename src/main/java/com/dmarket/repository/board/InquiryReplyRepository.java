package com.dmarket.repository.board;

import com.dmarket.domain.board.InquiryReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface InquiryReplyRepository extends JpaRepository<InquiryReply, Long> {
    void deleteByInquiryReplyId(@Param("inquiryReplyId") Long inquiryReplyId);

    void deleteByInquiryId(Long inquiryId);
}
