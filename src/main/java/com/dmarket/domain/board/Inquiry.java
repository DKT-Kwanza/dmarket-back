package com.dmarket.domain.board;

import com.dmarket.constant.InquiryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquiryId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private InquiryType inquiryType;

    private String inquiryTitle;

    @Column(columnDefinition="TEXT")
    private String inquiryContents;

    @Column(columnDefinition="TEXT")
    private String inquiryImg;

    private Boolean inquiryState;

    private LocalDateTime inquiryCreatedDate;


    @Builder
    public Inquiry(Long userId, InquiryType inquiryType, String inquiryTitle, String inquiryContents, String inquiryImg, Boolean inquiryState) {
        this.userId = userId;
        this.inquiryType = inquiryType;
        this.inquiryTitle = inquiryTitle;
        this.inquiryContents = inquiryContents;
        this.inquiryImg = inquiryImg;
        this.inquiryState = inquiryState;
        this.inquiryCreatedDate = LocalDateTime.now().withNano(0);
    }

    // 문의 답변시 status 변경
    public void updateStatus(Boolean status) {
        this.inquiryState = status;
    }
}
