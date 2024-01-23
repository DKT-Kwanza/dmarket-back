package com.dmarket.repository.user;

import com.dmarket.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserEmail(String userEmail);
}

