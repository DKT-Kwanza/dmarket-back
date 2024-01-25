package com.dmarket.dto.response;

import com.dmarket.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressResDto {
    private Integer userPostalCode;
    private String userAddress;
    private String userDetailedAddress;

    public UserAddressResDto(User user){
        this.userPostalCode = user.getUserPostalCode();
        this.userAddress = user.getUserAddress();
        this.userDetailedAddress = user.getUserAddressDetail();
    }

}
