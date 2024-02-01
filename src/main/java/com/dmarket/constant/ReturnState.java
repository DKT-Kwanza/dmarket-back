package com.dmarket.constant;

import java.util.Arrays;

public enum ReturnState {
    RETURN_REQUEST("반품 요청"),        //      OrderDetailState의 RETURN_REQUEST와 다르니 주의바랍니다
    COLLECT_ING("수거중"),
    COLLECT_COMPLETE("수거 완료"),
    REFUND_COMPLETE("환불 완료");

    public final String label;

    private ReturnState(String label) {
        this.label = label;
    }

    public static ReturnState fromLabel(String label) {
        return Arrays.stream(ReturnState.values())
                .filter(state -> state.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 요청 메시지: " + label));
    }
}
