package com.dmarket.dto.common;

import com.dmarket.dto.common.MileageReqDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MileageReqListDto {
    private Long mileageReqId;
    private LocalDateTime mileageReqDate;
    private Long userId;
    private String userName;
    private String userEmail;
    private String mileageReqReason;
    private Integer mileageCharge;
    private String mileageReqStatus;

    public MileageReqListDto(MileageReqDto mileageReqDto){
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