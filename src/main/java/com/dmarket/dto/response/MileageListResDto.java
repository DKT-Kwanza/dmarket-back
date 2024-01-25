package com.dmarket.dto.response;

import com.dmarket.dto.common.MileageDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MileageListResDto {
    private Integer totalPage;
    private List<MileageDto> mileageList;
}