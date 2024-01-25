package com.dmarket.domain.board;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String noticeTitle;

    @Column(columnDefinition="TEXT", nullable = false)
    private String noticeContents;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime noticeCreatedDate;


    @Builder
    public Notice(Long userId, String noticeTitle, String noticeContents) {
        this.userId = userId;
        this.noticeTitle = noticeTitle;
        this.noticeContents = noticeContents;
        this.noticeCreatedDate = LocalDateTime.now().withNano(0);
    }
}
