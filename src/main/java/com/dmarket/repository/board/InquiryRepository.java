package com.dmarket.repository.board;

import com.dmarket.constant.InquiryType;
import com.dmarket.domain.board.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    //문의 목록 조회(카테고리별)
    Page<Inquiry> findByInquiryType(InquiryType inquiryType, Pageable pageable);
}