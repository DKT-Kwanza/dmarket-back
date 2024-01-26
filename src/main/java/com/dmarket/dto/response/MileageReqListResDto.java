package com.dmarket.dto.response;

import com.dmarket.dto.common.MileageReqDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.dmarket.dto.common.MileageReqListDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MileageReqListResDto {
    private int totalPage;
    private List<MileageReqListDto> mileageReqList;
}