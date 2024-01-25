package com.dmarket.dto.response;
import java.time.LocalDateTime;
import com.dmarket.domain.product.Product;
import com.dmarket.domain.product.Qna;
import com.dmarket.domain.product.QnaReply;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Data
@Getter
@NoArgsConstructor
public class QnaResDto {
    private Long qnaId;
    private Long productId;
    private String productName;
    private String qnaTitle;
    private String qnaContents;
    private LocalDateTime qnaCreatedDate;
    private Boolean qnaIsSecret;
    private String qnaStatus;
    private String qnaReplyContents;
    private LocalDateTime qnaReplyDate;

    public QnaResDto(Qna qna, Product product, QnaReply qnaReply) {
        this.qnaId = qna.getQnaId();
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.qnaTitle = qna.getQnaTitle();
        this.qnaContents = qna.getQnaContents();
        this.qnaCreatedDate = qna.getQnaCreatedDate();
        this.qnaIsSecret = qna.getQnaSecret();
        this.qnaStatus = qna.getQnaState() ? "답변 완료" : "답변 대기";
        
        // QnaReply가 null이 아닐 때만 QnaReply 관련 정보를 설정합니다.
        if (qnaReply != null) {
            this.qnaReplyContents = qnaReply.getQnaReplyContents();
            this.qnaReplyDate = qnaReply.getQnaReplyDate();
        } else {
            // QnaReply가 null일 경우, QnaReply 관련 필드들을 null 혹은 기본값으로 설정할 수 있습니다.
            this.qnaReplyContents = null; // 또는 적절한 기본값
            this.qnaReplyDate = null; // 또는 적절한 기본값
        }
    }
    
    // getters and setters
}