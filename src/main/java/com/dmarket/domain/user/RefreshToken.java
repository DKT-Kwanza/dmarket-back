package com.dmarket.domain.user;

import com.dmarket.constant.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenId;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime refreshTokenExpiredDate;

    @Builder
    public RefreshToken(String userEmail, String refreshToken, LocalDateTime refreshTokenExpiredDate) {
        this.userEmail = userEmail;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiredDate = refreshTokenExpiredDate;
    }
}
