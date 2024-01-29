package com.dmarket.dto.common;

import com.dmarket.constant.MileageContents;
import com.dmarket.constant.MileageReqState;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MileageCommonDto {

    private Long mileageReqId;
    private LocalDateTime mileageReqDate;
    private Long userId;
    private String userName;
    private String userEmail;
    private MileageContents mileageReqReason;
    private Integer mileageCharge;
    private MileageReqState mileageReqStatus;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MileageDto {
        private LocalDateTime mileageChangeDate;
        private String mileageContents;
        private Integer changeMileage;
        private Integer remainMileage;

        @Builder
        public MileageDto(LocalDateTime mileageChangeDate, MileageContents mileageContents, Integer changeMileage, Integer remainMileage) {
            this.mileageChangeDate = mileageChangeDate;
            this.mileageContents = mileageContents.getLabel();
            this.changeMileage = changeMileage;
            this.remainMileage = remainMileage;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MileageReqListDto {
        private Long mileageReqId;
        private LocalDateTime mileageReqDate;
        private Long userId;
        private String userName;
        private String userEmail;
        private String mileageReqReason;
        private Integer mileageCharge;
        private String mileageReqStatus;

        public MileageReqListDto(MileageCommonDto mileageReqDto) {
            this.mileageReqId = mileageReqDto.getMileageReqId();
            this.mileageReqDate = mileageReqDto.getMileageReqDate();
            this.userId = mileageReqDto.getUserId();
            this.userName = mileageReqDto.getUserName();
            this.userEmail = mileageReqDto.getUserEmail();
            this.mileageReqReason = mileageReqDto.getMileageReqReason().getLabel();
            this.mileageCharge = mileageReqDto.getMileageCharge();
            this.mileageReqStatus = mileageReqDto.getMileageReqStatus().getLabel();
        }


    }
}
