package com.dmarket.repository.board;

import com.dmarket.constant.InquiryType;
import com.dmarket.domain.board.Inquiry;
import com.dmarket.dto.response.InquiryListResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    //문의 목록 조회(카테고리별)
    @Query("SELECT new com.dmarket.dto.response.InquiryListResDto(" +
            "i.inquiryId, i.inquiryTitle, i.inquiryContents, i.inquiryType, i.inquiryState, i.inquiryImg, i.inquiryCreatedDate, u.userName) " +
            "FROM Inquiry i " +
            "LEFT JOIN User u ON i.userId = u.userId " +
            "WHERE (:inquiryType is null OR i.inquiryType = :inquiryType) " +
            "ORDER BY i.inquiryCreatedDate DESC")
    Page<InquiryListResDto> findByInquiryType(@Param("inquiryType") InquiryType inquiryType, Pageable pageable);

    //문의 삭제
    void deleteByInquiryId(@Param("inquiryId") Long inquiryId);
}