package com.dmarket.dto.response;

import com.dmarket.constant.MileageContents;
import com.dmarket.constant.MileageReqState;
import com.dmarket.dto.common.MileageCommonDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class MileageResDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MileageReqListResDto {
        private Long mileageReqId;
        private LocalDateTime mileageReqDate;
        private Long userId;
        private String userName;
        private String userEmail;
        private MileageContents mileageReqReason;
        private Integer mileageCharge;
        private MileageReqState mileageReqStatus;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MileageListResDto {
        private Integer totalPage;
        private List<MileageCommonDto.MileageDto> mileageList;
    }
}
