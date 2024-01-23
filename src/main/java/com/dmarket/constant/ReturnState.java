package com.dmarket.constant;

public enum ReturnState {
    RETURN_REQUEST,         // 반품 요청     OrderDetailState의 RETURN_REQUEST와 다르니 주의바랍니다
    COLLECT_ING,            // 수거중
    COLLECT_COMPLETE,       // 수거 완료
    RETURN_COMPLETE         // 반품 완료     OrderDetailState의 RETURN_REQUEST와 다르니 주의바랍니다
}
