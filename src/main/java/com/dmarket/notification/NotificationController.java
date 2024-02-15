package com.dmarket.notification;

import com.dmarket.dto.response.CMResDto;
import com.dmarket.exception.ErrorCode;
import com.dmarket.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {
    private final JWTUtil jwtUtil;
    private final NotificationService notificationService;

    // sse 연결
    @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@PathVariable Long userId,
                                                @RequestHeader(value = "lastEventId", required = false, defaultValue = "") String lastEventId,
                                                HttpServletResponse response){
        log.info(lastEventId);
        return new ResponseEntity<>(notificationService.subscribe(userId, response), HttpStatus.OK);
    }

    // 유저 별 알림 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId){
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return new ResponseEntity<> (notifications, HttpStatus.OK);
    }

    // 알림 읽음 처리
    @PutMapping()
    public ResponseEntity<?> readNotification(HttpServletRequest request,
                                              @Valid @RequestBody NotificationReqDto notificationReqDto){
        ResponseEntity<?> authorization = checkAuthorization(notificationReqDto.getReceiver(), request);
        if(authorization != null){
            return authorization;
        }
        notificationService.readNotification(notificationReqDto);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 알림 전체 읽음 처리
    @PutMapping("/{userId}")
    public ResponseEntity<?> readAllNotifications(HttpServletRequest request,
                                                  @PathVariable Long userId) {
        ResponseEntity<?> authorization = checkAuthorization(userId, request);
        if(authorization != null){
            return authorization;
        }
        notificationService.readAllNotifications(userId);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 안읽은 알림 개수
    @GetMapping("/{userId}/unreadCount")
    public ResponseEntity<?> getUnreadCount(@PathVariable Long userId) {
        try {
            Long unreadCount = notificationService.getUnreadCount(userId);
            return ResponseEntity.ok().body(unreadCount);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("알림 카운트 오류");
        }
    }

    // 알림 일괄 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteNotifications(HttpServletRequest request,
                                                 @PathVariable Long userId){
        ResponseEntity<?> authorization = checkAuthorization(userId, request);
        if(authorization != null){
            return authorization;
        }
        notificationService.deleteAllNotifications(userId);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 인증 및 권한 검사 메서드
    private ResponseEntity<?> checkAuthorization(Long userId, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        System.out.println("userId = " + userId);
        String token = authorization.split(" ")[1];
        Long tokenUserId = jwtUtil.getUserId(token);
        System.out.println("tokenUserId = " + tokenUserId);
        if (!Objects.equals(tokenUserId, userId)) {
            return new ResponseEntity<>(CMResDto.errorRes(ErrorCode.FORBIDDEN), HttpStatus.FORBIDDEN);
        }
        return null; // 인증 및 권한 검사가 성공한 경우
    }
}
