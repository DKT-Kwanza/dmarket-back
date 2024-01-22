package com.dmarket.dto.response;

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
    private T data;

    @Builder
    public CMResDto(int code, String msg, T data) {
        this.code = code;
        this.time = LocalDateTime.now().withNano(0);
        this.msg = msg;
        this.data = data;
    }
}
