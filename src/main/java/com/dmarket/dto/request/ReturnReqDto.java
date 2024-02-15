package com.dmarket.dto.request;

import com.dmarket.constant.ReturnState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ReturnReqDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangeReturnStateDto {
        private String returnStatus;
    }
}
