package com.dmarket.dto.response;

import com.dmarket.domain.product.Qna;
import com.dmarket.domain.product.QnaReply;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QnaProductIdListResDto {
    private Long qnaId;
    private Boolean qnaIsSecret;
    private String qnaWriter;
    private String qnaTitle;
    private String qnaContents;
    private LocalDateTime qnaCreatedDate;
    private String qnaStatus;
    private LocalDateTime qnaReplyDate;
    private String qnaReplyContents;
    public QnaProductIdListResDto(Long qnaId, Boolean qnaIsSecret, String qnaWriter, String qnaTitle, String qnaContents, LocalDateTime qnaCreatedDate, Boolean qnaState, LocalDateTime qnaReplyDate, String qnaReplyContents) {
        this.qnaId = qnaId;
        this.qnaIsSecret = qnaIsSecret;
        this.qnaWriter = qnaWriter;
        this.qnaTitle = qnaTitle;
        this.qnaContents = qnaContents;
        this.qnaCreatedDate = qnaCreatedDate;
        this.qnaStatus = qnaState ? "답변 완료" : "답변 대기";
        this.qnaReplyDate = qnaReplyDate;
        this.qnaReplyContents = qnaReplyContents;
    }
}
