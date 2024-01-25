package com.dmarket.controller;

import com.dmarket.constant.FaqType;
import com.dmarket.domain.board.Faq;
import com.dmarket.dto.request.*;
import com.dmarket.dto.response.*;
import com.dmarket.service.AdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    AdminService adminService;

    @GetMapping("/GM")
    public String adminGMP() {
        return "Admin GM Page";
    }

    @GetMapping("/PM")
    public String adminPMP() {
        return "Admin PM Page";
    }

    @GetMapping("/SM")
    public String adminSMP() {
        return "Admin SM Page";
    }

    private void bindingResultErrorsCheck(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError fe : bindingResult.getFieldErrors()) {
                errorMap.put(fe.getField(), fe.getDefaultMessage());
            }
            throw new RuntimeException(errorMap.toString());
        }
    }

    // 사용자 삭제
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            adminService.deleteUserByUserId(userId);
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("사용자 삭제 완료").build(), HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            // 잘못된 요청에 대한 예외 처리
            log.warn("유효하지 않은 요청 메시지: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").build(), HttpStatus.BAD_REQUEST);

        } catch (AuthenticationException e) {
            // 인증 오류에 대한 예외 처리
            log.warn("유효하지 않은 인증" + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(401).msg("유효하지 않은 인증").build(), HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            // 기타 예외에 대한 예외 처리
            log.error("서버 내부 오류: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg("서버 내부 오류").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 사용자 조회
    @GetMapping("/admin-user")
    public ResponseEntity<?> getUsers(@RequestParam(value = "q", required = true) Integer dktNum) {
        try {
            List<UserResDto> userResDtos = adminService.getUsersFindByDktNum(dktNum);
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("사용자 목록 조회 완료").data(userResDtos).build(), HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            // 잘못된 요청에 대한 예외 처리
            log.warn("유효하지 않은 요청 메시지: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").build(), HttpStatus.BAD_REQUEST);

        } catch (AuthenticationException e) {
            // 인증 오류에 대한 예외 처리
            log.warn("유효하지 않은 인증" + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(401).msg("유효하지 않은 인증").build(), HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            // 기타 예외에 대한 예외 처리
            log.error("서버 내부 오류: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg("서버 내부 오류").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 공지사항 목록 조회
    @GetMapping("/board/notices")
    public ResponseEntity<?> getNotices() {
        try {
            List<NoticeResDto> noticeResDtos = adminService.getNotices();
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("공지사항 목록 조회 완료").data(noticeResDtos).build(), HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            // 잘못된 요청에 대한 예외 처리
            log.warn("유효하지 않은 요청 메시지: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").build(), HttpStatus.BAD_REQUEST);

        } catch (AuthenticationException e) {
            // 인증 오류에 대한 예외 처리
            log.warn("유효하지 않은 인증" + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(401).msg("유효하지 않은 인증").build(), HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            // 기타 예외에 대한 예외 처리
            log.error("서버 내부 오류: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg("서버 내부 오류").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 공지사항 작성
    @PostMapping("/board/notice")
    public ResponseEntity<?> postNotice(@Valid @RequestBody NoticeReqDto noticeReqDto,
            BindingResult bindingResult) {
        try {
            // request body 유효성 확인
            bindingResultErrorsCheck(bindingResult);

            // 공지사항 작성
            Long userId = noticeReqDto.getUserId();
            String noticeTitle = noticeReqDto.getNoticeTitle();
            String noticeContents = noticeReqDto.getNoticeContents();
            adminService.postNotice(userId, noticeTitle, noticeContents);

            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("공지사항 작성 완료").build(), HttpStatus.OK);
        } catch (RuntimeException e) {
            log.warn(e.getMessage(), e.getCause());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").data(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("서버 내부 오류").build(), HttpStatus.BAD_REQUEST);
        }
    }

    // 공지사항 삭제
    @DeleteMapping("/board/notice/{noticeId}")
    public ResponseEntity<?> deleteNotice(@PathVariable Long noticeId) {
        try {
            adminService.deleteNoticeByNoticeId(noticeId);
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("공지사항 삭제 완료").build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // 잘못된 요청에 대한 예외 처리
            log.warn("유효하지 않은 요청 메시지: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").build(), HttpStatus.BAD_REQUEST);

        } catch (AuthenticationException e) {
            // 인증 오류에 대한 예외 처리
            log.warn("유효하지 않은 인증" + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(401).msg("유효하지 않은 인증").build(), HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            // 기타 예외에 대한 예외 처리
            log.error("서버 내부 오류: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg("서버 내부 오류").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // faq 조회
    @GetMapping("/board/faq")
    public ResponseEntity<?> getFaqs(@RequestParam(required = false, value = "page", defaultValue = "0") int pageNo,
                                     @RequestParam(required = false, value = "size", defaultValue = "10") int pageSize,
                                     @RequestParam(required = false, value = "type") FaqType faqType) {
        try {
            if (pageNo < 0 || pageSize <= 0) {
                return new ResponseEntity<>(CMResDto.builder()
                        .code(400).msg("유효하지 않은 페이지 또는 크기").build(), HttpStatus.BAD_REQUEST);
            }

            Page<Faq> faqsPage = adminService.getAllFaqs(faqType, PageRequest.of(pageNo, pageSize));
            Page<FaqListResDto> mappedFaqs = adminService.mapToFaqListResDto(faqsPage);

            CMResDto<Page<FaqListResDto>> response = CMResDto.<Page<FaqListResDto>>builder().code(200).msg("FAQ 조회 성공").data(mappedFaqs).build();

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.warn("유효하지 않은 요청 메시지:" + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder().code(400).msg("유효하지 않은 요청 메시지").build(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error retrieving FAQs: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder().code(500).msg("서버 내부 오류").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // faq 삭제
    @DeleteMapping("/board/faq/{faqId}")
    public ResponseEntity<?> deleteFaq(@PathVariable(name="faqId") Long faqId) {
        try {
            adminService.deleteFaqByFaqId(faqId);
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("FAQ 삭제 완료").build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // 잘못된 요청에 대한 예외 처리
            log.warn("유효하지 않은 요청 메시지: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").build(), HttpStatus.BAD_REQUEST);

        } catch (AuthenticationException e) {
            // 인증 오류에 대한 예외 처리
            log.warn("유효하지 않은 인증" + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(401).msg("유효하지 않은 인증").build(), HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            // 기타 예외에 대한 예외 처리
            log.error("서버 내부 오류: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg("서버 내부 오류").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
