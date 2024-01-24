package com.dmarket.repository.user;

import com.dmarket.domain.user.User;
import com.dmarket.dto.response.UserResDto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    void deleteByUserId(@Param("userId") Long userId);

    @Query("SELECT new com.dmarket.dto.response.UserResDto(u) FROM User u WHERE u.userDktNum = :userDktNum")
    List<UserResDto> getUsersFindByDktNum(@Param("userDktNum") Integer userDktNum);
}
