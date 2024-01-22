package com.dmarket.domain.board;

import jakarta.persistence.*;
import lombok.AccessLevel;
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
}
