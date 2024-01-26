package com.dmarket.controller;

import com.dmarket.constant.FaqType;
import com.dmarket.domain.board.Faq;
import com.dmarket.constant.ReturnState;
import com.dmarket.domain.product.Product;
import com.dmarket.dto.request.*;
import com.dmarket.dto.response.*;
import com.dmarket.service.AdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.time.*;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    AdminService adminService;

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

    // 공지사항 목록 조회 with 페이지네이션
    @GetMapping("/board/notices")
    public ResponseEntity<?> getNotices(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {

        try {
            page = page > 0 ? page - 1 : page;
            if (page < 0 || size <= 0) {
                return new ResponseEntity<>(CMResDto.builder().code(400).msg("유효하지 않은 페이지 또는 크기").build(),
                        HttpStatus.BAD_REQUEST);
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<NoticeResDto> noticeResDtos = adminService.getNotices(pageable);
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
            BindingResult bindingResult,
            @RequestParam(required = false, value = "page", defaultValue = "0") int page,
            @RequestParam(required = false, value = "size", defaultValue = "10") int size) {
        try {
            page = page > 0 ? page - 1 : page;

            // request body 유효성 확인
            bindingResultErrorsCheck(bindingResult);

            // 공지사항 작성
            Long userId = noticeReqDto.getUserId();
            String noticeTitle = noticeReqDto.getNoticeTitle();
            String noticeContents = noticeReqDto.getNoticeContents();
            Pageable pageable = PageRequest.of(page, size);
            adminService.postNotice(userId, noticeTitle, noticeContents, pageable);

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

            CMResDto<Page<FaqListResDto>> response = CMResDto.<Page<FaqListResDto>>builder().code(200).msg("FAQ 조회 성공")
                    .data(mappedFaqs).build();

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.warn("유효하지 않은 요청 메시지:" + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder().code(400).msg("유효하지 않은 요청 메시지").build(),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error retrieving FAQs: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder().code(500).msg("서버 내부 오류").build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // faq 삭제
    @DeleteMapping("/board/faq/{faqId}")
    public ResponseEntity<?> deleteFaq(@PathVariable(name = "faqId") Long faqId) {
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

    // FAQ 등록
    @PostMapping("/board/faq")
    public ResponseEntity<?> postFaq(@Valid @RequestBody FaqReqDto faqReqDto,
            BindingResult bindingResult) {
        try {
            // request body 유효성 확인
            bindingResultErrorsCheck(bindingResult);

            // FAQ 등록
            FaqType faqType = faqReqDto.getFaqType();
            String faqQuestion = faqReqDto.getFaqTitle();
            String faqAnswer = faqReqDto.getFaqContents();
            Long faqId = adminService.postFaq(faqType, faqQuestion, faqAnswer);

            FaqListResDto faqListResDto = new FaqListResDto(faqId, faqType, faqQuestion, faqAnswer);

            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("FAQ 등록 완료").data(faqListResDto).build(), HttpStatus.OK);
        } catch (RuntimeException e) {
            log.warn(e.getMessage(), e.getCause());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").data(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("서버 내부 오류").build(), HttpStatus.BAD_REQUEST);
        }
    }

    // 상품 수정
    @PutMapping("/products/{productId}")
    public ResponseEntity<?> updateProduct(@Valid @RequestBody ProductReqDto productReqDto,
            BindingResult bindingResult) {
        try {
            // request body 유효성 확인
            bindingResultErrorsCheck(bindingResult);

            // 상품 수정
            adminService.updateProduct(productReqDto);

            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("상품 수정 완료").build(), HttpStatus.OK);
        } catch (RuntimeException e) {
            log.warn(e.getMessage(), e.getCause());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").data(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("서버 내부 오류").build(), HttpStatus.BAD_REQUEST);
        }
    }

    // 상품 상세 정보 조회
    @GetMapping("/products/{productId}")
    public ResponseEntity<?> getProductInfo(@PathVariable Long productId) {
        try {
            Long userId = 1L;
            ProductInfoResDto res = adminService.getProductInfo(productId, userId);
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("상품 정보 가져오기 완료").data(res).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(CMResDto.builder().code(400).msg("유효하지 않은 요청 메시지").build(),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(CMResDto.builder().code(500).msg("서버 내부 오류").build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 상품 리뷰
    @GetMapping("/products/review")
    public ResponseEntity<?> getReviews(@RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        try {
            page = page > 0 ? page - 1 : page;
            if (page < 0 || size <= 0) {
                return new ResponseEntity<>(CMResDto.builder().code(400).msg("유효하지 않은 페이지 또는 크기").build(),
                        HttpStatus.BAD_REQUEST);
            }
            Pageable pageable = PageRequest.of(page, size);
            Page<AdminReviewsResDto> adminReviewsResDtos = adminService.getProductReviews(pageable);
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("상품 리뷰 조회 완료").data(adminReviewsResDtos).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(CMResDto.builder().code(400).msg("유효하지 않은 요청 메시지").build(),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(CMResDto.builder().code(500).msg("서버 내부 오류").build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 반품 상태 변경
    @PutMapping("/orders/returns/{returnId}")
    public ResponseEntity<?> changeReturnStatus(@PathVariable Long returnId, @RequestParam String returnStatus) {
        try {
            adminService.updateReturnState(returnId, ReturnState.valueOf(returnStatus));
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("반품 상태 변경 완료").build(), HttpStatus.OK);
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

    // 새로운 상품 추가
    @PostMapping("/product")
    public ResponseEntity<?> addNewProduct(@RequestBody List<ProductListDto> productList) {
        try {
            adminService.saveProductList(productList);
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("상품 등록 완료").build(), HttpStatus.OK);
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

    //배송 상태 변경
    @PutMapping("/orders/{detailId}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long detailId, @Valid @RequestBody OrderStatusReqDto requestDto, BindingResult bindingResult) {
        try {
            // request body 유효성 확인
            bindingResultErrorsCheck(bindingResult);

            String orderStatus = requestDto.getOrderStatus();
            adminService.updateOrderDetailState(detailId, orderStatus);

            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("배송 상태 변경 완료").build(), HttpStatus.OK);
        } catch (RuntimeException e) {
            log.warn(e.getMessage(), e.getCause());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").data(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("서버 내부 오류").build(), HttpStatus.BAD_REQUEST);
        }
    }

}
