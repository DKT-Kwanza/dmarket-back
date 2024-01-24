package com.dmarket.domain.board;

import com.dmarket.constant.FaqType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.dmarket.constant.FaqType.USER;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Faq {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long faqId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private FaqType faqType;

    @Column(columnDefinition="TEXT")
    private String faqQuestion;

    @Column(columnDefinition="TEXT")
    private String faqAnswer;



    @Builder
    public Faq(FaqType faqType, String faqQuestion, String faqAnswer) {
        this.faqType = faqType;
        this.faqQuestion = faqQuestion;
        this.faqAnswer = faqAnswer;
    }
}
