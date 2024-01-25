package com.dmarket.repository.board;

import com.dmarket.domain.board.Inquiry;
import com.dmarket.dto.response.UserInquiryAllResDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    @Query(value = "select new com.dmarket.dto.response.UserInquiryAllResDto(i.inquiryId, i.inquiryType, i.inquiryTitle, i.inquiryContents, i.inquiryImg, i.inquiryCreatedDate, i.inquiryState, ir.inquiryReplyContents, ir.inquiryReplyDate)" +
            " from Inquiry i " +
            " left join InquiryReply ir on ir.inquiryId = i.inquiryId" +
            " where i.userId = :userId")
    List<UserInquiryAllResDto> findUserInquiryAllByUserId(@Param("userId") Long userId);
}
