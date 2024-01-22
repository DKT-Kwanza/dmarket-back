package com.dmarket.domain.board;

import com.dmarket.constant.FaqType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
