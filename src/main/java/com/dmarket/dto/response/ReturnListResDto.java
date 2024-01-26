package com.dmarket.dto.response;

import org.springframework.data.domain.Page;

import com.dmarket.dto.common.ReturnDto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Data
@Getter
@Setter 
@NoArgsConstructor
public class ReturnListResDto {
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
