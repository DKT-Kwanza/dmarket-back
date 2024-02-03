package com.dmarket.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notify")
public class NotificationController {
    private final NotificationService notificationService;

    // sse 연결
    @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@PathVariable Long userId,
                                                @RequestHeader(value = "lastEventId", required = false, defaultValue = "") String lastEventId){
        log.info(lastEventId);
        return new ResponseEntity<>(notificationService.subscribe(userId), HttpStatus.OK);
    }

    // 유저 별 알림 조회
    @GetMapping("/{userId}/notifications")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId){
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return new ResponseEntity<> (notifications, HttpStatus.OK);
    }
}
