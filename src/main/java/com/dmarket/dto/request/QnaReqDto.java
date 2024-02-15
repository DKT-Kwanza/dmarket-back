package com.dmarket.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class QnaReqDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QnaReplyReqDto {
        private String qnaReplyContents;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QnaWriteReqDto {
        private Long userId;
        private String qnaTitle;
        private String qnaContents;
        private Boolean qnaIsSecret;
    }
}

