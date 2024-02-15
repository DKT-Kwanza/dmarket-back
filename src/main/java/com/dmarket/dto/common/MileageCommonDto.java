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

        @Builder
        public MileageReqListDto(Long mileageReqId, LocalDateTime mileageReqDate, Long userId, String userName, String userEmail, MileageContents mileageReqReason,
                                 Integer mileageReqAmount, MileageReqState mileageReqState) {
            this.mileageReqId = mileageReqId;
            this.mileageReqDate = mileageReqDate;
            this.userId = userId;
            this.userName = userName;
            this.userEmail = userEmail;
            this.mileageReqReason = mileageReqReason.getLabel();
            this.mileageCharge = mileageReqAmount;
            this.mileageReqStatus = mileageReqState.getLabel();
        }


    }
}
