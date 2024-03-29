package com.dmarket.jwt;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public Long getUserId(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userId", Long.class);
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public String getEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    public String getType(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("type", String.class);
    }

    public String getAuthHeader(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    public String getToken(String header) {
        if (header == null) {
            throw new IllegalArgumentException("헤더에 Authorization이 없습니다.");
        }
        if (!header.startsWith("Bearer")) throw new IllegalArgumentException("토큰이 Bearer로 시작하지 않습니다.");

        return header.substring("Bearer ".length());
    }

    public void isExpired(String token) {
        Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration();
    }

    public String createAccessJwt(Long userId, String email, String role) {
        return Jwts.builder()
                .claim("userId",userId)
                .claim("role", role)
                .claim("email", email)
                .claim("type", "ATK")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshJwt(Long userId) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("type", "RTK")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 240 * 60 * 60 * 1000))
                .signWith(secretKey)
                .compact();
    }

}
