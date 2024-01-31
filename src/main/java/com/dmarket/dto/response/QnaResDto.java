package com.dmarket.dto.response;

import com.dmarket.domain.product.Product;
import com.dmarket.domain.product.Qna;
import com.dmarket.domain.product.QnaReply;
import com.dmarket.domain.user.User;
import com.dmarket.dto.common.QnaDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class QnaResDto {

    // qna 작성
    @Getter
    public static class QnaWriteResponseDto {
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

    @Data
    @Getter
    @NoArgsConstructor
    public class QnaTotalListResDto {
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

        public QnaTotalListResDto(Qna qna, Product product, QnaReply qnaReply) {
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
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QnaProductIdListResDto {
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

    @Getter
    @AllArgsConstructor
    public static class QnaListResDto {
        private Integer totalPage;
        private List<QnaDto> qnaList;
    }

    @Getter
    @NoArgsConstructor
    public static class QnaDetailResDto {
        private Long qnaId;
        private String qnaWriter;
        private String productName;
        private String qnaTitle;
        private String qnaContents;
        private String qnaStatus;
        private Boolean qnaIsSecret;
        private LocalDateTime qnaCreatedDate;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Long qnaReplyId;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String qnaReplyContents;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private LocalDateTime qnaReplyDate;

        public QnaDetailResDto(Qna qna, Product product, User user, QnaReply qnaReply) {
            this.qnaId = qna.getQnaId();
            this.qnaWriter = user.getUserName();
            this.productName = product.getProductName();
            this.qnaTitle = qna.getQnaTitle();
            this.qnaContents = qna.getQnaContents();
            this.qnaStatus = qna.getQnaState() ? "답변 완료" : "답변 대기";
            this.qnaIsSecret = qna.getQnaSecret();
            this.qnaCreatedDate = qna.getQnaCreatedDate();

            // QnaReply가 null이 아닐 때만 QnaReply 관련 정보를 설정합니다.
            if (qnaReply != null) {
                this.qnaReplyId = qnaReply.getQnaReplyId();
                this.qnaReplyContents = qnaReply.getQnaReplyContents();
                this.qnaReplyDate = qnaReply.getQnaReplyDate();
            } else {
                // QnaReply가 null일 경우, QnaReply 관련 필드들을 null 혹은 기본값으로 설정할 수 있습니다.
                this.qnaReplyId = null;
                this.qnaReplyContents = null; // 또는 적절한 기본값
                this.qnaReplyDate = null; // 또는 적절한 기본값
            }
        }
    }
}

