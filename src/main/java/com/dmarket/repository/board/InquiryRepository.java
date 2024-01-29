package com.dmarket.repository.board;

import com.dmarket.constant.InquiryType;
import com.dmarket.domain.board.Inquiry;
import com.dmarket.dto.common.InquiryCommonDto;
import com.dmarket.dto.response.InquiryResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    //문의 목록 조회(카테고리별)
    @Query("SELECT new com.dmarket.dto.response.InquiryResDto$InquiryListResDto(" +
            "i.inquiryId, i.inquiryTitle, i.inquiryContents, i.inquiryType, i.inquiryState, i.inquiryImg, i.inquiryCreatedDate, u.userName) " +
            "FROM Inquiry i " +
            "LEFT JOIN User u ON i.userId = u.userId " +
            "WHERE (:inquiryType is null OR i.inquiryType = :inquiryType) " + // inquiryType이 null인 경우(inquiryType 파라미터가 제공되지 않은 경우) 조건 항상 true로 설정
            "ORDER BY i.inquiryCreatedDate DESC")
    Page<InquiryResDto.InquiryListResDto> findByInquiryType(@Param("inquiryType") InquiryType inquiryType, Pageable pageable);

    //문의 삭제
    void deleteByInquiryId(@Param("inquiryId") Long inquiryId);

    // 문의 전체 조회
    @Query(value = "select new com.dmarket.dto.response.InquiryResDto$UserInquiryAllResDto(i.inquiryId, i.inquiryType, i.inquiryTitle, i.inquiryContents, i.inquiryImg, i.inquiryCreatedDate, i.inquiryState, ir.inquiryReplyContents, ir.inquiryReplyDate)" +
            " from Inquiry i " +
            " left join InquiryReply ir on ir.inquiryId = i.inquiryId" +
            " where i.userId = :userId" +
            " order by i.inquiryCreatedDate desc")
    List<InquiryResDto.UserInquiryAllResDto> findUserInquiryAllByUserId(@Param("userId") Long userId);

    //문의 답변 등록
    @Query("SELECT NEW com.dmarket.dto.common.InquiryCommonDto$InquiryDetailsDto(" +
            "i.inquiryId, i.inquiryTitle, i.inquiryContents, i.inquiryType, i.inquiryState, u.userName, i.inquiryImg, i.inquiryCreatedDate, ir.inquiryReplyContents " +
            ") " +
            "FROM Inquiry i " +
            "LEFT JOIN InquiryReply ir ON i.inquiryId = ir.inquiryId " +
            "LEFT JOIN User u ON i.userId = u.userId " +
            "WHERE ir.inquiryReplyId = :inquiryReplyId")
    InquiryCommonDto.InquiryDetailsDto findInquiryDetailsByInquiryReplyId(@Param("inquiryReplyId") Long inquiryReplyId);
}