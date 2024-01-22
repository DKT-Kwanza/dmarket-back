package com.dmarket.domain.board;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QnaReply {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qnaReplyId;

    private Long userId;
    private Long qnaId;

    @Column(columnDefinition="TEXT")
    private String qnaReplyContents;

    private LocalDateTime qnaReplyDate;
}
