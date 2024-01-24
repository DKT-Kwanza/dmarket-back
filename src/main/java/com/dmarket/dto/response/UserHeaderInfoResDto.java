package com.dmarket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserHeaderInfoResDto {
    private String userName;
    private LocalDate userJoinDate;
    private Integer userMileage;
}
