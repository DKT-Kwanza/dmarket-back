package com.dmarket.dto.response;

import com.dmarket.domain.user.User;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchUser {
        private Long userId;
        private String userName;
        private String userEmail;
        private Integer userDktNum;
        private String userRole; // Role 타입을 String으로 변환하여 저장할 것이므로 String 타입으로 선언합니다.
        private LocalDate userJoinDate;

    }

    @RequiredArgsConstructor
    public static class CustomUserDetails implements UserDetails {

        private final User userEntity;


        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            Collection<GrantedAuthority> collection = new ArrayList<>();
            collection.add(new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return String.valueOf(userEntity.getUserRole());
                }
            });
            return collection;
        }

        @Override
        public String getPassword() {
            return userEntity.getUserPassword();
        }

        @Override
        public String getUsername() {
            return userEntity.getUserName();
        }

        public Long getUserId() {
            return userEntity.getUserId();
        }

        public String getEmail() {
            return userEntity.getUserEmail();
        }

        @Override
        public boolean isAccountNonExpired() {

            return true;
        }

        @Override
        public boolean isAccountNonLocked() {

            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {

            return true;
        }

        @Override
        public boolean isEnabled() {

            return true;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TotalAdminResDto {
        private int totalManagerCount;
        private int GMCount;
        private int SMCount;
        private int PMCount;
        private List<AdminResDto.ManagerInfoDto> managerList;

        public TotalAdminResDto(int totalManagerCount, int GMCount, int SMCount, int PMCount, List<AdminResDto.ManagerInfoDto> managerList) {
            this.totalManagerCount = totalManagerCount;
            this.GMCount = GMCount;
            this.SMCount = SMCount;
            this.PMCount = PMCount;
            this.managerList = managerList;
        }

    }
}
