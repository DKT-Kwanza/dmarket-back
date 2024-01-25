package com.dmarket.constant;

import lombok.Getter;

@Getter
public enum MileageReqType {
    REFUND("환불"), // 환불
    CHARGE("마일리지 충전");  // 충전

    private final String label;

    private MileageReqType(String label) {
        this.label = label;
    }
}
