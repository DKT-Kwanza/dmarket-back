package com.dmarket.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class JoinReqDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)  //최소 8자리
    private String password;

    @NotNull
    private Integer dktNum;  //사원번호

    @NotBlank
    private String name;  //이름

    @NotBlank
    private String phoneNum;  //전화번호

    @NotNull
    private LocalDate joinDate;  //입사일

    @NotNull
    private Integer postalCode;  //우편번호

    @NotBlank
    private String userAddress;  //주소

    @NotBlank
    private String userAddressDetail;  //상세주소
}