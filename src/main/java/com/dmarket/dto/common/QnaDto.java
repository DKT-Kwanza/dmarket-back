package com.dmarket.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class QnaDto {

    private Long qnaId;
    private String productName;
    private String qnaTitle;
    private String qnaWriter;
    private LocalDateTime qnaCreatedDate;
    private String qnaStatus;
    private Boolean qnaIsSecret;

    public QnaDto(Long qnaId, String productName, String qnaTitle, String qnaWriter, LocalDateTime qnaCreatedDate, Boolean qnaStatus, Boolean qnaIsSecret) {
        this.qnaId = qnaId;
        this.productName = productName;
        this.qnaTitle = qnaTitle;
        this.qnaWriter = qnaWriter;
        this.qnaCreatedDate = qnaCreatedDate;
        this.qnaStatus = qnaStatus ? "답변 완료" : "답변 대기";
        this.qnaIsSecret = qnaIsSecret;
    }
}
