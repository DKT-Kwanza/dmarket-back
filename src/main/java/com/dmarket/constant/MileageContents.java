package com.dmarket.constant;

import lombok.Getter;

@Getter
public enum MileageContents {
    REFUND("환불"), // 환불
    CHARGE("마일리지 충전");  // 충전

    private final String label;

    private MileageContents(String label) {
        this.label = label;
    }
}