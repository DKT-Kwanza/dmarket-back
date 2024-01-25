package com.dmarket.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressReqDto {
    private Integer userPostalCode;
    private String userAddress;
    private String userDetailedAddress;
}
