package com.dmarket.dto.response;

import com.dmarket.domain.user.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class QnaWriteResponseDto {
    private Boolean qnaIsSecret;
    private String qnaWriter;
    private String qnaTitle;
    private LocalDateTime qnaCreatedDate;
    private Boolean qnaStatus;

    public QnaWriteResponseDto(Boolean qnaIsSecret, String qnaWriter, String qnaTitle, LocalDateTime qnaCreatedDate, Boolean qnaStatus) {
        this.qnaIsSecret = qnaIsSecret;
        this.qnaWriter = qnaWriter;
        this.qnaTitle = qnaTitle;
        this.qnaCreatedDate = qnaCreatedDate;
        this.qnaStatus = qnaStatus;
    }
}
