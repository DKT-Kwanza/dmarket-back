package com.dmarket.dto.common;

import com.dmarket.constant.MileageContents;
import com.dmarket.constant.MileageReqState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MileageReqDto {
    private Long mileageReqId;
    private LocalDateTime mileageReqDate;
    private Long userId;
    private String userName;
    private String userEmail;
    private MileageContents mileageReqReason;
    private Integer mileageCharge;
    private MileageReqState mileageReqStatus;
}
