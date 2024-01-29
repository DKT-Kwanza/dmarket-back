package com.dmarket.constant;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum OrderDetailState {
    ORDER_COMPLETE("결제 완료"),
    DELIVERY_READY("배송 준비"),
    DELIVERY_ING("배송중"),
    DELIVERY_COMPLETE("배송 완료"),
    ORDER_CANCEL("주문 취소"),
    RETURN_REQUEST("환불/반품신청"),
    RETURN_COMPLETE("환불/반품완료");

    public final String label;

    private OrderDetailState(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    public static OrderDetailState fromLabel(String label) {
        return Arrays.stream(OrderDetailState.values())
                .filter(e -> e.label.equals(label))
                .findFirst()
                .orElse(null);
    }
}