package com.dmarket.constant;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum FaqType {
    // InquiryType 과 동일하니 주의바랍니다.
    USER("회원"),
    MILEAGE("마일리지"),
    RETURN("반품/환불"),
    ORDER("주문/결제");
    private final String label;

    private FaqType(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    public static FaqType fromLabel(String label) {
        return Arrays.stream(FaqType.values())
                .filter(e -> e.label.equals(label))
                .findFirst()
                .orElse(null);
    }
}
