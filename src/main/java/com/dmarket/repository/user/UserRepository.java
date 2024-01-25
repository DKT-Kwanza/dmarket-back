package com.dmarket.repository.user;

import com.dmarket.domain.user.User;
import com.dmarket.dto.response.UserHeaderInfoResDto;
import com.dmarket.dto.response.UserInfoResDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select new com.dmarket.dto.response.UserInfoResDto(u.userName, u.userEmail, u.userDktNum, u.userPhoneNum, u.userAddress, u.userAddressDetail, u.userPostalCode, u.userJoinDate)" +
                    " from User u " +
                    " where u.userId = :userId")
    UserInfoResDto findUserInfoByUserId(@Param("userId")Long userId);

    // 마이페이지 서브헤더 사용자 정보 및 마일리지 조회
    @Query(value = "select new com.dmarket.dto.response.UserHeaderInfoResDto (u.userName, u.userJoinDate, u.userMileage) " +
                    "from User u " +
                    "where u.userId = :userId")
    UserHeaderInfoResDto findUserHeaderInfoByUserId(Long userId);
    User findByUserEmail(String userEmail);
}

