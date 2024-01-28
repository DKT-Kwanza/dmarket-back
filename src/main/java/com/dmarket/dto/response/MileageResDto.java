package com.dmarket.dto.response;

import com.dmarket.dto.common.MileageCommonDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class MileageResDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MileageReqListResDto {
        private int totalPage;
        private List<MileageCommonDto.MileageReqListDto> mileageReqList;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MileageListResDto {
        private Integer totalPage;
        private List<MileageCommonDto.MileageDto> mileageList;
    }
}
