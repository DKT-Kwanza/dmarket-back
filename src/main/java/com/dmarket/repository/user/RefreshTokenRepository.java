package com.dmarket.repository.user;

import com.dmarket.domain.user.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    boolean existsById(String refreshToken);

    void deleteById(String refreshToken);

    Optional<RefreshToken> findByAccessToken(String refreshToken);
}
