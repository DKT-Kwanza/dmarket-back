package com.dmarket.dto.common;

import com.dmarket.constant.MileageContents;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MileageDto {
    private LocalDateTime mileageChangeDate;
    private String mileageContents;
    private Integer changeMileage;
    private Integer remainMileage;

    @Builder
    public MileageDto(LocalDateTime mileageChangeDate, MileageContents mileageContents, Integer changeMileage, Integer remainMileage){
        this.mileageChangeDate = mileageChangeDate;
        this.mileageContents = mileageContents.getLabel();
        this.changeMileage = changeMileage;
        this.remainMileage = remainMileage;
    }
}
