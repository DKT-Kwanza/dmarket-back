package com.dmarket.dto.response;

import com.dmarket.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
        this.time = LocalDateTime.now().withNano(0);
        this.msg = msg;
        this.data = data;
    }

    // 반환 데이터 없는 성공 response
    public static <T> CMResDto<?> successNoRes(){
        return CMResDto.builder()
                .code(200).msg("성공")
                .build();
    }

    // 반환 데이터 있는 성공 response
    public static <T> CMResDto<?> successDataRes(T data){
        return CMResDto.builder()
                .code(200).msg("성공").data(data)
                .build();
    }

    // 에러 response
    public static <T> CMResDto<?> errorRes(ErrorCode errorCode){
        return CMResDto.builder()
                .code(errorCode.getCode()).msg(errorCode.getMsg())
                .build();
    }

    // 에러 response(msg 직접 지정)
    public static <T> CMResDto<?> errorWithMsgRes(ErrorCode errorCode, String msg){
        return CMResDto.builder()
                .code(errorCode.getCode()).msg(msg)
                .build();
    }
}
