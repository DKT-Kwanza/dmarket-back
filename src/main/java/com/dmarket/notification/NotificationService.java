package com.dmarket.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SseEmitters sseEmitters;
    private final NotificationRepository notificationRepository;
    // timeout 시간 설정
    private static final long TIMEOUT = 60 * 1000L;
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        String id = userId + "_" + System.currentTimeMillis();

        sseEmitters.add(id, emitter);
        log.info("emitter 생성: {}", emitter);
        log.info(id);


        Map<String, Object> testContent = new HashMap<>();
        testContent.put("content", "connected!");

        sendToClient(emitter, "test", id, testContent);

//        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
//        if (!lastEventId.isEmpty()) {
//            Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId));
//            eventCaches.entrySet().stream()
//                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
//                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
//        }

        // 타임아웃 시 emitter 만료
        emitter.onTimeout(() -> {
            log.info("onTimeout callback");
            emitter.complete();
        });

        // broken pipeline
        emitter.onError(throwable -> {
            log.error("[sse] SseEmitters 파일 add 메서드 : {}", throwable.getMessage());
            log.error("", throwable);
            emitter.complete();
        });

        emitter.onCompletion(() -> {
            log.info("onCompletion callback");
            // emitter 만료(혹은 연결 끊길) 시 제거
            // 재연결 시 emitter 다시 생성되기 때문
            sseEmitters.delete(id);
        });

        return emitter;
    }

    private void sendToClient(SseEmitter emitter, String name, String id, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name(name)
                    .data(data));
        } catch (IOException exception) {
            sseEmitters.delete(id);
            throw new RuntimeException("연결 오류!");
        }
    }

    @Transactional
    public void send(SendNotificationEvent noti) {
        Notification notification = notificationRepository.save(Notification.create(noti));
        log.info("저장됨");

        String receiverId = noti.getReceiver() + "_";
        log.info(receiverId);

        // 해당 회원의 emitter 모두 찾아서 이벤트 전송
        Map<String, SseEmitter> emitters = sseEmitters.findEmitter(receiverId);
        log.info(emitters.entrySet().toString());

        emitters.forEach(
            (key, emitter) -> {
                sendToClient(emitter, noti.getName(), noti.getEventId(), notification);
                log.info("알림 전송 완료");
            }
        );
    }

    // 알림 읽음 처리
    @Transactional
    public void readNotification(NotificationReqDto notificationReqDto) {
        Notification notification = notificationRepository.findByNotiIdAndReceiver(notificationReqDto.getNotiId(), notificationReqDto.getReceiver())
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 알림"));
        notification.updateIsRead(true);
    }
}