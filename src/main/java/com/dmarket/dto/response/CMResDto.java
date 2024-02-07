package com.dmarket.dto.response;

import com.dmarket.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@NoArgsConstructor
public class CMResDto<T> {

    private int code;
    private LocalDateTime time;
    private String msg;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @Builder
    public CMResDto(int code, String msg, T data) {
        this.code = code;
        this.time = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        this.msg = msg;
        this.data = data;
    }

    // 반환 데이터 없는 성공 response
    public static CMResDto<String> successNoRes() {
        return CMResDto.<String>builder()
                .code(200).msg("성공").data("성공")
                .build();
    }

    // 반환 데이터 있는 성공 response
    public static <T> CMResDto<T> successDataRes(T data) {
        return CMResDto.<T>builder()
                .code(200).msg("성공").data(data)
                .build();
    }

    // 에러 response
    public static CMResDto<String> errorRes(ErrorCode errorCode) {
        return CMResDto.<String>builder()
                .code(errorCode.getCode()).msg(errorCode.getMsg()).data("에러 발생: " + errorCode.getMsg())
                .build();
    }

    // 에러 response(msg 직접 지정)
    public static CMResDto<String> errorWithMsgRes(ErrorCode errorCode, String msg) {
        return CMResDto.<String>builder()
                .code(errorCode.getCode()).msg(msg).data("에러 발생: " + msg)
                .build();
    }
}