package com.dmarket.dto.response;

import com.dmarket.dto.common.ReturnDto;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

public class ReturnResDto {
    @Data
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReturnListResDto {
        private Long returnReqCount; // 반품 요청 수
        private Long returnColCount; // 반품 수거 중 수
        private Long colConfCount; // 반품 수거 완료 수
        private Page<ReturnDto> returnList;

        public ReturnListResDto(Long returnReqCount, Long returnColCount, Long colConfCount) {
            this.returnReqCount = returnReqCount;
            this.returnColCount = returnColCount;
            this.colConfCount = colConfCount;
        }

    }
}
