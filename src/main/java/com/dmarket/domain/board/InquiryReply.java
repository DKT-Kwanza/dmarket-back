package com.dmarket.domain.board;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InquiryReply {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquiryReplyId;

    private Long userId;
    private Long inquiryId;

    @Column(columnDefinition="TEXT")
    private String inquiryReplyContents;
    private LocalDateTime inquiryReplyDate;
}
