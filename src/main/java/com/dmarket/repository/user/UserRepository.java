package com.dmarket.repository.user;

import com.dmarket.constant.Role;
import com.dmarket.domain.user.User;
import com.dmarket.dto.response.UserResDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u.userEmail FROM User u WHERE u.userId = :userId")
    String findUserEmailByUserId(Long userId);

    @Query("SELECT u.userId FROM User u WHERE u.userEmail = :userEmail")
    Long findUserIdByUserEmail(@Param("userEmail") String userEmail);

    List<User> getUsersFindByUserDktNum(@Param("userDktNum") Integer userDktNum);

    @Query(value = "select new com.dmarket.dto.response.UserResDto$UserInfo(u.userName, u.userEmail, u.userDktNum, u.userPhoneNum, u.userAddress, u.userAddressDetail, u.userPostalCode, u.userJoinDate)"
            +
            " from User u " +
            " where u.userId = :userId")
    UserResDto.UserInfo findUserInfoByUserId(@Param("userId") Long userId);

    // 마이페이지 서브헤더 사용자 정보 및 마일리지 조회
    @Query(value = "select new com.dmarket.dto.response.UserResDto$UserHeaderInfo (u.userName, u.userJoinDate, u.userMileage) "
            +
            "from User u " +
            "where u.userId = :userId")
    UserResDto.UserHeaderInfo findUserHeaderInfoByUserId(Long userId);

    @Modifying
    @Query("UPDATE User u SET u.userMileage = u.userMileage + :calculatedAmount " +
            "WHERE u.userId IN (" +
            "SELECT o.userId FROM Order o " +
            "JOIN OrderDetail od ON o.orderId = od.orderId " +
            "JOIN Return r ON od.orderDetailId = r.orderDetailId " +
            "WHERE r.returnId = :returnId)")
    void updateUserMileageByReturnId(@Param("returnId") Long returnId,
                                     @Param("calculatedAmount") Integer calculatedAmount);

    // 전체 사용자 마일리지 초기화
    @Modifying
    @Query("update User u set u.userMileage = :initMileage")
    void resetUserMileage(Integer initMileage);

    // 주문 취소시 마일리지 추가
    @Modifying
    @Query(value = "update User u set u.userMileage = u.userMileage + :cancelPrice where u.userId = :userId")
    void updateUserMileageByCancel(@Param("userId") Long userId, @Param("cancelPrice") Integer cancelPrice);

    User findByUserId(Long userId);

    User findByUserEmail(String userEmail);

    User findUserNameByUserId(Long userId);

    // 사원 번호로 user 검색
    User findByUserDktNum(Integer userDktNum);

    // User 별로 사용자 집계
    List<User> findAllByUserRoleIsNot(Role userRole);

    boolean existsByUserEmail(String userEmail);

    boolean existsByUserDktNum(Integer userDktNum);

    void deleteByUserId(@Param("userId") Long userId);
}
