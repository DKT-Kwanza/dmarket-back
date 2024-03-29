package com.dmarket.domain.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
    private Boolean qnaSecret;

    @Column(nullable = false)
    private Boolean qnaState;

    @Column(nullable = false)
    private LocalDateTime qnaCreatedDate;


    @Builder
    public Qna(Long userId, Long productId, String qnaTitle, String qnaContents, Boolean qnaSecret, Boolean qnaState) {
        this.userId = userId;
        this.productId = productId;
        this.qnaTitle = qnaTitle;
        this.qnaContents = qnaContents;
        this.qnaSecret = qnaSecret;
        this.qnaState = qnaState;
        this.qnaCreatedDate = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }

    // 답변 상태 업데이트
    public void updateState(Boolean state){
        this.qnaState = state;
    }
}
