package com.dmarket.repository.user;

import com.dmarket.domain.user.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Boolean existsByRefreshToken(String token);
    @Transactional
    void deleteByUserEmail(String email);
}