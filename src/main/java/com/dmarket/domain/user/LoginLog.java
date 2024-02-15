package com.dmarket.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loginLogId;

    @Column(nullable = false)
    private LocalDateTime loginLogCreatedDate;

    @Column(nullable = false, columnDefinition="TEXT")
    private String loginLogContents;

    public LoginLog(String loginLogContents) {
        this.loginLogCreatedDate = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        this.loginLogContents = loginLogContents;
    }
}
