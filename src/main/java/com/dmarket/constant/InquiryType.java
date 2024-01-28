package com.dmarket.constant;

import java.util.Arrays;

public enum InquiryType {
    USER("회원"),
    MILEAGE("마일리지"),
    RETURN("반품/환불"),
    ORDER("주문/결제");

    public final String label;

    private InquiryType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static InquiryType fromLabel(String label) {
        return Arrays.stream(InquiryType.values())
                .filter(e -> e.label.equals(label))
                .findFirst()
                .orElse(null);
    }
}
