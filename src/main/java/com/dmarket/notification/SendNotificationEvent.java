package com.dmarket.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Slf4j
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class SendNotificationEvent {
    private final SseEmitters sseEmitters;
    private final Repository repository;

    private String name;    //이벤트 이름

    private String eventId;  //알림 번호

    private Long receiver;  //알림을 받는 유저의 정보

    private String content; //알람의 내용

    private String url; //해당 알림 클릭시 이동할 mapping url

    private Boolean isRead; //알림 열람에 대한 여부

    private LocalDateTime notificationCreatedDate;

    public static SendNotificationEvent of(String name, Long receiver, String content, String url){
        return SendNotificationEvent.builder()
                .name(name)
                .eventId(receiver + "_" + System.currentTimeMillis())
                .receiver(receiver)
                .content(content)
                .url(url)
                .isRead(false)
                .notificationCreatedDate(LocalDateTime.now().withNano(0))
                .build();
    }
}