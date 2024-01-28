package com.dmarket.dto.response;

import java.time.LocalDate;

import com.dmarket.domain.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserResDto {

    @Data
    @Getter
    @NoArgsConstructor
    public static class Search {
        private Long userId;
        private String userName;
        private String userEmail;
        private String userRole;
        private Integer userDktNum;
        private LocalDate userJoinDate;

        public Search(User user) {
            this.userId = user.getUserId();
            this.userName = user.getUserName();
            this.userEmail = user.getUserEmail();
            this.userRole = user.getUserRole().name();
            this.userDktNum = user.getUserDktNum();
            this.userJoinDate = user.getUserJoinDate();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserAddress {
        private Integer userPostalCode;
        private String userAddress;
        private String userDetailedAddress;

        public UserAddress(User user) {
            this.userPostalCode = user.getUserPostalCode();
            this.userAddress = user.getUserAddress();
            this.userDetailedAddress = user.getUserAddressDetail();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserHeaderInfo {
        private String userName;
        private LocalDate userJoinDate;
        private Integer userMileage;
    }

    @Data
    @Builder
    public static class UserInfo {
        private String userName;
        private String userEmail;
        private Integer userDktNum;
        private String userPhoneNum;
        private String userAddress;
        private String userAddressDetail;
        private Integer userPostalCode;
        private LocalDate userJoinDate;
    }

}
