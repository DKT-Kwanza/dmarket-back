package com.dmarket.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 유저 별 알림 조회
    List<Notification> findByReceiverOrderByNotificationCreatedDateDesc(Long receiver);

    // 특정 유저 알림 전체 읽음 처리
    @Modifying
    @Query("update Notification n set n.isRead = true where n.receiver = :userId")
    void readAllNotification(Long userId);

    Long countByReceiverAndIsRead(Long userId, boolean isRead);

    // 특정 유저 알림 전체 삭제
    @Modifying
    void deleteAllByReceiver(Long userId);
}
