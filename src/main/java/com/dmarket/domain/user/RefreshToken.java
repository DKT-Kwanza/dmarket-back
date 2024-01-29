package com.dmarket.domain.user;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


@Getter
@RedisHash(value =  "refreshToken", timeToLive = 240 * 60 * 60 * 1000)
public class RefreshToken {

    @Id
    private String refreshToken;
    private String accessToken;
    private String userEmail;

    @Builder
    public RefreshToken(String refreshToken, String accessToken,String userEmail) {
        this.userEmail = userEmail;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }
}
