package com.dmarket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class TotalAdminResDto {
    private int totalManagerCount;
    private int GMCount;
    private int SMCount;
    private int PMCount;
    private List<ManagerInfoDto> managerList;

    public TotalAdminResDto(int totalManagerCount, int GMCount, int SMCount, int PMCount, List<ManagerInfoDto> managerList) {
        this.totalManagerCount = totalManagerCount;
        this.GMCount = GMCount;
        this.SMCount = SMCount;
        this.PMCount = PMCount;
        this.managerList = managerList;
    }

}
