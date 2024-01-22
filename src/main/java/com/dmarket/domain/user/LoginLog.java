package com.dmarket.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loginLog;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime loginLogCreatedDate;

    @Column(nullable = false, columnDefinition="TEXT")
    private String loginLogContents;
}
