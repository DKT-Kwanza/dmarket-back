package com.dmarket.domain.board;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InquiryReply {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquiryReplyId;

    private Long inquiryId;

    @Column(columnDefinition="TEXT")
    private String inquiryReplyContents;

    private LocalDateTime inquiryReplyDate;


    @Builder
    public InquiryReply(Long inquiryId, String inquiryReplyContents) {
        this.inquiryId = inquiryId;
        this.inquiryReplyContents = inquiryReplyContents;
        this.inquiryReplyDate = LocalDateTime.now().withNano(0);
    }
}
