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
public class Notice {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    private Long userId;

    private String noticeTitle;

    @Column(columnDefinition="TEXT")
    private String noticeContents;

    private LocalDateTime noticeCreatedDate;


    @Builder
    public Notice(Long userId, String noticeTitle, String noticeContents) {
        this.userId = userId;
        this.noticeTitle = noticeTitle;
        this.noticeContents = noticeContents;
        this.noticeCreatedDate = LocalDateTime.now().withNano(0);
    }
}
