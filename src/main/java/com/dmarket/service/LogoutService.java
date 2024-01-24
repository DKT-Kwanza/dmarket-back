package com.dmarket.service;

import com.dmarket.jwt.JWTUtil;
import com.dmarket.repository.user.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        String email = jwtUtil.getEmail(token);

        refreshTokenRepository.deleteByUserEmail(email);
    }
}

