package com.dmarket.domain.board;

import com.dmarket.constant.InquiryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
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

    private LocalDateTime inquiryCreatedDate;
    private LocalDateTime inquiryUpdatedDate;
    private Boolean inquiryState;
}
