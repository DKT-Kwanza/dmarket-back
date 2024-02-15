package com.dmarket.constant;

import lombok.Getter;

@Getter
public enum MileageReqState {
    PROCESSING("처리전"), // 요청 처리 전
    APPROVAL("승인"),   // 요청 승인
    REFUSAL("거부");    // 요청 거부

    private final String label;

    private MileageReqState(String label) {
        this.label = label;
    }
}
