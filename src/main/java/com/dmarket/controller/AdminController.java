package com.dmarket.controller;

import com.dmarket.constant.InquiryType;
import com.dmarket.constant.FaqType;
import com.dmarket.domain.board.InquiryReply;
import com.dmarket.domain.board.Faq;
import com.dmarket.dto.common.InquiryDetailsDto;
import com.dmarket.dto.common.OrderDetailStateCountsDto;
import com.dmarket.dto.request.*;
import com.dmarket.dto.response.*;
import com.dmarket.service.AdminService;
import java.util.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

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
            List<UserResDto.Search> userResDtos = adminService.getUsersFindByDktNum(dktNum);
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

    //마일리지 충전 요청/처리 내역 조회
    @GetMapping("/users/mileage-history")
    public ResponseEntity<?> getMileageRequests(@RequestParam(required = true, value = "status", defaultValue = "PROCESSING") String status,
                                                @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo,
                                                Pageable pageable){
        try{
            pageNo = pageNo > 0 ? pageNo-1 : pageNo;
            MileageReqListResDto requests = adminService.getMileageRequests(pageable, status, pageNo);

            log.info("데이터 조회 완료");
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("성공").data(requests).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // 잘못된 요청에 대한 예외 처리
            log.warn("유효하지 않은 요청 메시지:" + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // 기타 예외에 대한 예외 처리
            log.error("서버 내부 오류: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //마일리지 충전 요청 승인
    @PutMapping("/users/mileage/approval/{mileageReqId}")
    public ResponseEntity<?> approveMileageReq(@PathVariable(name = "mileageReqId") Long mileageReqId){
        try{
            //true인 경우 승인, false인 경우 거부
            adminService.approveMileageReq(mileageReqId, true);
            log.info("데이터 변경 완료");
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("성공").build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // 잘못된 요청에 대한 예외 처리
            log.warn("유효하지 않은 요청 메시지:" + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // 기타 예외에 대한 예외 처리
            log.error("서버 내부 오류: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //마일리지 충전 요청 거부
    @PutMapping("/users/mileage/refusal/{mileageReqId}")
    public ResponseEntity<?> refusalMileageReq(@PathVariable(name = "mileageReqId") Long mileageReqId){
        try{
            //true인 경우 승인, false인 경우 거부
            adminService.approveMileageReq(mileageReqId, false);
            log.info("데이터 변경 완료");
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("성공").build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // 잘못된 요청에 대한 예외 처리
            log.warn("유효하지 않은 요청 메시지:" + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // 기타 예외에 대한 예외 처리
            log.error("서버 내부 오류: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
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

    // 상품 QnA 전체 조회 api
    @GetMapping("/products/qna")
    public ResponseEntity<?> getQnAList(
            @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {
        try {
            pageNo = pageNo > 0 ? pageNo - 1 : pageNo;
            QnaResDto.QnaListResDto qnaList = adminService.getQnaList(pageNo);
            return new ResponseEntity<>(CMResDto.builder().code(200).msg("성공").data(qnaList).build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(CMResDto.builder().code(200).msg(e.getMessage()).build(), HttpStatus.OK);
        }
    }

    // 상품 QnA 상세 + 답변 조회 api
    @GetMapping("/products/qna/{qnaId}")
    public ResponseEntity<?> getQnADetailed(@PathVariable Long qnaId) {
        QnaResDto.QnaDetailResDto qnaDetail = adminService.getQnADetail(qnaId);
        return new ResponseEntity<>(CMResDto.builder()
                .code(200).msg("성공").data(qnaDetail).build(), HttpStatus.OK);
    }

    // 상품 QnA 답변 작성 api
    @PostMapping("/products/qna/{qnaId}")
    public ResponseEntity<?> writeQnaReply(@PathVariable Long qnaId ,@Valid @RequestBody QnaReplyReqDto qnaReplyReqDto, BindingResult bindingResult){
        try {
            bindingResultErrorsCheck(bindingResult);

            QnaResDto.QnaDetailResDto qnaDetail = adminService.createQnaReply(qnaId, qnaReplyReqDto.getQnaReplyContents());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("성공").data(qnaDetail).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg(e.getMessage()).build(), HttpStatus.OK);
        }
    }

    // 상품 QnA 답변 작성 api
    @DeleteMapping("/products/qna/reply/{qnaReplyId}")
    public ResponseEntity<?> writeQnaReply(@PathVariable Long qnaReplyId){
        try {
            QnaResDto.QnaDetailResDto qnaDetail = adminService.deleteQnaReply(qnaReplyId);
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("성공").data(qnaDetail).build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg(e.getMessage()).build(), HttpStatus.OK);
        }
    }

    // 반품 상태 변경
    @PutMapping("/orders/returns/{returnId}")
    public ResponseEntity<?> changeReturnStatus(@PathVariable Long returnId, @RequestBody ChangeReturnStateDto changeReturnStateDto) {
        try {
            String returnState = changeReturnStateDto.getReturnStatus();
            adminService.updateReturnState(returnId, returnState);
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

    // 문의 목록 조회(카테고리별)
    @GetMapping("/board/inquiry")
    public ResponseEntity<?> getInquiries(
            @RequestParam(required = false, value = "type") InquiryType inquiryType,
            @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo,
            @RequestParam(required = false, value = "size", defaultValue = "10") int pageSize) {
        try {
            if (pageNo < 0 || pageSize <= 0) {
                return new ResponseEntity<>(CMResDto.builder()
                        .code(400).msg("검증되지 않은 페이지").build(), HttpStatus.BAD_REQUEST);
            }

            Page<InquiryListResDto> mappedInquiries = adminService.getAllInquiriesByType(inquiryType,
                    PageRequest.of(pageNo, pageSize));

            CMResDto<Page<InquiryListResDto>> response = CMResDto.<Page<InquiryListResDto>>builder()
                    .code(200).msg("문의 목록").data(mappedInquiries).build();

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.warn("유효하지 않은 요청 메시지: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder().code(400).msg("유효하지 않은 요청 메시지").build(),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("서버 내부 오류: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder().code(500).msg("서버 내부 오류").build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // 문의 삭제
    @DeleteMapping("/board/inquiry/{inquiryId}")
    public ResponseEntity<?> deleteInquiry(@PathVariable Long inquiryId) {
        try {
            boolean isDeleted = adminService.deleteInquiry(inquiryId);

            if (isDeleted) {
                return new ResponseEntity<>(CMResDto.builder().code(200).msg("Inquiry 삭제 성공").build(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(CMResDto.builder().code(404).msg("삭제할 대상이 없습니다.").build(),
                        HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            log.warn("유효하지 않은 요청 메시지:" + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder().code(400).msg("유효하지 않은 요청 메시지").build(),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error deleting Inquiry: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder().code(500).msg("서버 내부 오류").build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // 문의 답변 등록
    @PostMapping("/board/inquiry/reply/{inquiryId}")
    public ResponseEntity<CMResDto<InquiryDetailsDto>> postInquiryReply(
            @PathVariable Long inquiryId,
            @RequestBody InquiryReplyRequestDto inquiryReplyRequestDto) {
        try {
            InquiryReply inquiryReply = InquiryReply.builder()
                    .inquiryId(inquiryId)
                    .inquiryReplyContents(inquiryReplyRequestDto.getInquiryReplyContents())
                    .build();

            InquiryReply savedInquiryReply = adminService.createInquiryReply(inquiryReply);
            InquiryDetailsDto inquiryDetails = adminService.getInquiryDetails(savedInquiryReply.getInquiryReplyId());

            return ResponseEntity.ok(CMResDto.<InquiryDetailsDto>builder().code(200).msg("답변 작성 완료").data(inquiryDetails).build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(CMResDto.<InquiryDetailsDto>builder().code(400).msg("유효하지 않은 요청 메시지").build());
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CMResDto.<InquiryDetailsDto>builder().code(401).msg("유효하지 않은 인증").build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CMResDto.<InquiryDetailsDto>builder().code(500).msg("서버 내부 오류").build());
        }
    }

    // 문의 답변 삭제
    @DeleteMapping("/board/inquiry/reply/{inquiryReplyId}")
    public ResponseEntity<?> deleteInquiryReply(@PathVariable Long inquiryReplyId) {
        try {
            boolean isDeleted = adminService.deleteInquiryReply(inquiryReplyId);

            if (isDeleted) {
                return new ResponseEntity<>(CMResDto.builder().code(200).msg("Inquiry Reply 삭제 성공").build(),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(CMResDto.builder().code(404).msg("삭제할 대상이 없습니다.").build(),
                        HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            log.warn("유효하지 않은 요청 메시지:" + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder().code(400).msg("유효하지 않은 요청 메시지").build(),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error deleting Inquiry Reply: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder().code(500).msg("서버 내부 오류").build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }











    // 배송 상태 변경경
    @PutMapping("/orders/{detailId}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long detailId,
            @Valid @RequestBody OrderStatusReqDto requestDto, BindingResult bindingResult) {
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

    // 상품 옵션 삭제
    @DeleteMapping("/products/{productId}/{optionId}")
    public ResponseEntity<?> deleteOption(@PathVariable(name = "productId") Long productId,
            @PathVariable(name = "optionId") Long optionId) {
        try {
            adminService.deleteOptionByOptionId(optionId);
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("상품 옵션 삭제 완료").build(), HttpStatus.OK);
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

    // 상품 목록 조회
    @GetMapping("/products/categories/{categoryId}")
    public ResponseEntity<?> getProductsListAdmin(@PathVariable(name = "categoryId") Long categoryId) {
        try {
            List<ProductListAdminResDto> productListAdminResDto = adminService.getProductListByCateogryId(categoryId);
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("관리자 상품 목록 조회 완료").data(productListAdminResDto).build(), HttpStatus.OK);
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

    // if:False -> 그룹별 관리자 조회
    // if:True -> 사원 검색
    @GetMapping("/admin-users")
    public ResponseEntity<?> getAdmins(@RequestParam(value = "q", required = false) Integer dktNum) {
        try {
            if (dktNum != null){
                UserResDto.SearchUser searchUserRes = adminService.searchUser(dktNum);
                return new ResponseEntity<>(CMResDto.builder()
                        .code(200).msg("사원 검색").data(searchUserRes).build(), HttpStatus.OK);
            }else {
                TotalAdminResDto adminUserResponse = adminService.getAdminUserDetails();
                return new ResponseEntity<>(CMResDto.builder()
                        .code(200).msg("관리자 조회 성공").data(adminUserResponse).build(), HttpStatus.OK);
            }
        } catch (RuntimeException e) {
            log.warn(e.getMessage(), e.getCause());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").data(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("서버 내부 오류").build(), HttpStatus.BAD_REQUEST);
        }
    }

    // 권한 부여한
    // 사용힌 ChangeRoleReqDto -> UserReqDto.ChangeRole로 변경
    @PutMapping("/admin-users/{userId}")
    public ResponseEntity<?> changeRole(@PathVariable Long userId,
                                        @RequestBody UserReqDto.ChangeRole newRole){
        try {
            adminService.changeRole(userId,newRole);
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("관리자 조회 성공").build(), HttpStatus.OK);
        } catch (RuntimeException e) {
            log.warn(e.getMessage(), e.getCause());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").data(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("서버 내부 오류").build(), HttpStatus.BAD_REQUEST);
        }
    }

    // 상태별 반품 목록 조회
    @GetMapping("/orders/returns")
    public ResponseEntity<?> getReturnsList(@RequestParam(required = true, value = "status") String returnStatus,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        try {
            page = page > 0 ? page - 1 : page;
            if (page < 0 || size <= 0) {
                return new ResponseEntity<>(CMResDto.builder().code(400).msg("유효하지 않은 페이지 또는 크기").build(),
                        HttpStatus.BAD_REQUEST);
            }
            Pageable pageable = PageRequest.of(page, size);
            ReturnListResDto returnListResDto = adminService.getReturns(returnStatus, pageable);

            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("관리자 상품 목록 조회 완료").data(returnListResDto).build(), HttpStatus.OK);
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

    //마일리지 환불
    @PutMapping("/cancel-order-details")
    public ResponseEntity<?> putRefund(@Valid @RequestBody RefundReqDto RefundReqDto, BindingResult bindingResult) {
        try {
            bindingResultErrorsCheck(bindingResult);

            adminService.putRefund(RefundReqDto);
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("마일리지 환불 완료").build(), HttpStatus.OK);
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


    // 주문 취소 목록 조회
    ///api/admin/cancel-order-details

    @GetMapping("/cancel-order-details")
    public ResponseEntity<?> getCancledOrder(){
        try {
            List<OrderResDto.OrderCancelResDto> orderCancleList = adminService.orderCancle();
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("취소 목록 조회 완료").data(orderCancleList).build(), HttpStatus.OK);
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


    //재고 추가
    @PutMapping("/products/stock")
    public ResponseEntity<?> addProductStock(@RequestBody StockReqDto stockReqDto) {
        try {
            adminService.addProductStock(stockReqDto);

            // Retrieve and return updated product information
            Long productId = stockReqDto.getProductId();
            ProductInfoOptionResDto productInfoOptionResDto = adminService.getProductInfoWithOption(productId);

            return ResponseEntity.ok(CMResDto.builder()
                    .code(200)
                    .msg("상품 재고 추가 완료")
                    .data(Collections.singletonList(productInfoOptionResDto))
                    .build());
        }
        catch (IllegalArgumentException e) {
            log.warn("유효하지 않은 요청 메시지:" + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder().code(400).msg("유효하지 않은 요청 메시지").build(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error deleting Inquiry Reply: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder().code(500).msg("서버 내부 오류").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // 배송 목록 조회
    @GetMapping("/api/admin/orders")
    public ResponseEntity<CMResDto<Map<String, Object>>> getOrdersByStatus(@RequestParam String status) {
        try {
            OrderDetailStateCountsDto statusCounts = adminService.getOrderDetailStateCounts();
            List<OrderListAdminResDto> orderList = adminService.getOrdersByStatus(status);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("confPayCount", statusCounts.getOrderCompleteCount());
            responseData.put("preShipCount", statusCounts.getDeliveryReadyCount());
            responseData.put("InTransitCount", statusCounts.getDeliveryIngCount());
            responseData.put("delivCompCount", statusCounts.getDeliveryCompleteCount());
            responseData.put("orderList", orderList);

            return ResponseEntity.ok(CMResDto.<Map<String, Object>>builder()
                    .code(200)
                    .msg("배송 조회 완료")
                    .data(responseData)
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(CMResDto.<Map<String, Object>>builder()
                    .code(400)
                    .msg("유효하지 않은 요청 메시지")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CMResDto.<Map<String, Object>>builder()
                    .code(500)
                    .msg("서버 내부 오류")
                    .build());
        }
    }
    // ---배송 목록 조회---


}
