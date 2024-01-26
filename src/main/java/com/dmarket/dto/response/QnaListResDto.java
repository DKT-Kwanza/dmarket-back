package com.dmarket.dto.response;

import com.dmarket.dto.common.QnaDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class QnaListResDto {
    private Integer totalPage;
    private List<QnaDto> qnaList;
}
