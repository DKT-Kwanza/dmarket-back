package com.dmarket.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventHandler {
    private final NotificationService notificationService;
    @Async
    @TransactionalEventListener
    public void sendNotification(sendNotificationEvent notification){
        notificationService.send(notification);
    }
}