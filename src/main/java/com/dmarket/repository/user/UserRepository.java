package com.dmarket.repository.user;

import com.dmarket.domain.user.User;
import com.dmarket.dto.response.UserInfoResDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select new com.dmarket.dto.response.UserInfoResDto(u.userName, u.userEmail, u.userDktNum, u.userPhoneNum, u.userAddress, u.userAddressDetail, u.userPostalCode, u.userJoinDate)" +
            " from User u " +
            " where u.userId = :userId")
    UserInfoResDto findUserInfoByUserId(@Param("userId")Long userId);

    User findByUserEmail(String userEmail);
}

