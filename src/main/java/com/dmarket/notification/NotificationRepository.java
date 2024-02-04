package com.dmarket.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 유저 별 알림 조회
    List<Notification> findByReceiverOrderByNotificationCreatedDateDesc(Long receiver);

    Long countByReceiverAndIsRead(Long userId, boolean isRead);
}
