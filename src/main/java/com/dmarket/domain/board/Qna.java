package com.dmarket.domain.board;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Qna {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qnaId;

    private Long userId;
    private Long productId;

    @Column(nullable = false)
    private String qnaTitle;

    @Column(nullable = false, columnDefinition="TEXT")
    private String qnaContents;

    @Column(nullable = false)
    private LocalDateTime qnaCreatedDate;
    private LocalDateTime qnaUpdatedDate;

    @Column(nullable = false)
    private Boolean qnaSecret;

    @Column(nullable = false)
    private Boolean qnaState;
}
