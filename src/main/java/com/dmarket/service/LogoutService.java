package com.dmarket.service;

import com.dmarket.domain.user.RefreshToken;
import com.dmarket.dto.response.CMResDto;
import com.dmarket.jwt.JWTUtil;
import com.dmarket.repository.user.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LogoutService implements LogoutHandler {


    private final JWTUtil jwtUtil;

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.Authentication authentication) {
        String authorization = request.getHeader("Authorization");
        String token = authorization.split(" ")[1];

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByAccessToken(token);
        if (refreshToken.isPresent()) {
            refreshTokenRepository.deleteById(refreshToken.get().getRefreshToken());
        }else{
            System.out.println("사용자 정보 오류");
        }
    }
}

