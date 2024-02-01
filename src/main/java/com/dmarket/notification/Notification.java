package com.dmarket.notification;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notiId;

    private Long receiver; //알림을 받는 유저의 정보

    private String content; //알람의 내용

    private String url; //해당 알림 클릭시 이동할 mapping url

    private Boolean isRead; //알림 열람에 대한 여부

    private LocalDateTime notificationCreatedDate;

    public static Notification create(Long receiver, String content, String url) {
        return Notification.builder()
                .receiver(receiver)
                .content(content)
                .url(url)
                .isRead(false)
                .notificationCreatedDate(LocalDateTime.now().withNano(0))
                .build();
    }
}
