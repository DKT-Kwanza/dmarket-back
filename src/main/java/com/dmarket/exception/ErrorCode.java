package com.dmarket.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    BAD_REQUEST(400, "유효하지 않은 요청"),
    INVALID_REQUEST_BODY(400, "올바르지 않는 Request Body"),
    INVALID_TYPE_REQUEST_VALUE(400, "올바르지 않은 타입의 Request Value"),
    MISSING_REQUEST_PARAM(400, "필수 파라미터 존재하지 않음"),
    INVALID_USER_PASSWORD(400, "비밀번호 불일치"),
    USER_MODIFY_PASSWORD_FAILURE(400, "기존 비밀번호와 일치"),
    INVALID_RATING_PARAM(400, "잘못된 리뷰 평점 필터"),
    INVALID_STATE_PARAM(400, "잘못된 상태 값"),
    INVALID_SEARCH_VALUE(400, "검색 값이 비어있음"),
    INVALID_EMAIL_CODE(400, "인증 코드가 일치하지 않음"),
    INVALID_INQUIRY_TYPE(400, "잘못된 문의 타입"),

    UNAUTHORIZED(401, "로그인이 필요한 서비스"),
    FAIL_LOGIN(401, "아이디, 비밀번호 오류"),

    FORBIDDEN(403, "접근 권한 없음"),

    NOT_FOUND(404, "잘못된 경로"),
    NOT_VALID_PATH_VALUE(404, "잘못된 Path Variable"),
    USER_NOT_FOUND(404, "존재하지 않는 사용자"),
    PRODUCT_NOT_FOUND(404, "존재하지 않는 상품"),
    CATEGORY_NOT_FOUND(404, "존재하지 않는 카테고리"),
    REVIEW_NOT_FOUND(404, "존재하지 않는 리뷰"),
    QNA_NOT_FOUND(404, "존재하지 않는 상품 QnA"),
    INQUIRY_NOT_FOUND(404, "존재하지 않는 문의"),
    REPLY_NOT_FOUND(404, "존재하지 않는 답변"),
    REQUEST_NOT_FOUND(404, "존재하지 않는 요청 내역"),
    ORDER_NOT_FOUND(404, "존재하지 않는 주문 내역"),
    RETURN_NOT_FOUND(404, "존재하지 않는 반품 내역"),
    CART_ITEM_NOT_FOUND(404, "존재하지 않는 장바구니 아이템"),
    WISH_ITEM_NOT_FOUND(404, "존재하지 않는 위시 아이템"),
    STATE_NOT_FOUND(404, "존재하지 않는 상태값"),

    METHOD_NOT_ALLOWED(405, "잘못된 Http Method"),

    ALREADY_SAVED_USER(409, "이미 존재하는 사용자"),
    ALREADY_SAVED_PRODUCT(409, "이미 존재하는 상품"),
    ALREADY_SAVED_REVIEW(409, "이미 작성된 리뷰"),
    ALREADY_SAVED_WISH(409, "이미 위시리스트에 등록된 상품"),
    ALREADY_SAVED_REQUEST(409, "이미 존재하는 요청 내역"),
    ALREADY_SAVED_REPLY(409, "이미 답변된 문의"),

    INTERNAL_SERVER_ERROR(500, "서버 내부 오류");

    private final int code;
    private final String msg;

    private ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
