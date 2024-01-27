package com.dmarket.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_REQUEST_BODY(400, "올바르지 않는 Request Body"),
    USER_NOT_FOUND(400, "존재하지 않는 사용자"),
    INVALID_USER_PASSWORD(400, "비밀번호 불일치"),
    USER_MODIFY_PASSWORD_FAILURE(400, "기존 비밀번호와 일치"),
    PRODUCT_NOT_FOUND(400, "존재하지 않는 상품"),
    REVIEW_NOT_FOUND(400, "존재하지 않는 리뷰"),
    QNA_NOT_FOUND(400, "존재하지 않는 상품 QnA"),
    INQUIRY_NOT_FOUND(400, "존재하지 않는 문의"),
    ORDER_NOT_FOUND(400, "존재하지 않는 주문내역"),
    CART_ITEM_NOT_FOUND(400, "존재하지 않는 장바구니 아이템"),
    WISH_ITEM_NOT_FOUND(400, "존재하지 않는 위시 아이템"),
    INVALID_PAGING_PARAM(400, "잘못된 페이지 값"),
    INVALID_RATING_PARAM(400, "잘못된 리뷰 평점 필터"),

    UNAUTHORIZED(401, "로그인이 필요한 서비스"),
    FAIL_LOGIN(401, "아이디, 비밀번호 오류"),

    FORBIDDEN(403, "접근 권한 없음"),

    NOT_FOUND(404, "잘못된 경로"),

    METHOD_NOT_ALLOWED(405, "잘못된 Http Method"),

    ALREADY_SAVED_USER(409, "이미 존재하는 사용자"),
    ALREADY_SAVED_PRODUCT(409, "이미 존재하는 상품"),
    ALREADY_SAVED_REVIEW(409, "이미 작성된 리뷰"),
    ALREADY_SAVED_WISH(409, "이미 위시리스트에 등록된 상품"),
    ALREADY_SAVED_REQUEST(409, "이미 존재하는 요청 내역"),

    INTERNAL_SERVER_ERROR(500, "서버 내부 오류");

    private final int code;
    private final String msg;

    private ErrorCode(int code, String msg){
        this.code = code;
        this.msg = msg;
    }
}
