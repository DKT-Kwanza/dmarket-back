package com.dmarket.constant;

public enum OrderDetailState {
    ORDER_COMPLETE("결제 완료"),
    DELIVERY_READY("배송 준비"),
    DELIVERY_ING("배송 중"),
    DELIVERY_COMPLETE("배송 완료"),
    ORDER_CANCEL("주문 취소"),
    RETURN_REQUEST("환불/반품신청"),
    RETURN_COMPLETE("환불/반품완료");

    public final String label;

    private OrderDetailState(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}