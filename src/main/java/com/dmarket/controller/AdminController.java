package com.dmarket.controller;

import com.dmarket.constant.FaqType;
import com.dmarket.constant.InquiryType;
import com.dmarket.domain.board.Faq;
import com.dmarket.domain.board.InquiryReply;
import com.dmarket.dto.common.*;
import com.dmarket.dto.request.*;
import com.dmarket.dto.response.*;
import com.dmarket.dto.common.InquiryCommonDto;
import com.dmarket.dto.common.MileageCommonDto;
import com.dmarket.dto.common.OrderCommonDto;
import com.dmarket.dto.common.QnaDto;
import com.dmarket.exception.ErrorCode;
import com.dmarket.jwt.JWTUtil;
import com.dmarket.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final JWTUtil jwtUtil;

    // 사용자 삭제
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId, HttpServletRequest request) {
        adminService.deleteUserByUserId(userId);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 사용자 조회
    @GetMapping("/admin-user")
    public ResponseEntity<?> getUsers(@RequestParam(value = "q", required = true) Integer dktNum) {
        List<UserResDto.Search> userResDtos = adminService.getUsersFindByDktNum(dktNum);
        return new ResponseEntity<>(CMResDto.successDataRes(userResDtos), HttpStatus.OK);
    }

    // 공지사항 목록 조회 with 페이지네이션
    @GetMapping("/board/notices")
    public ResponseEntity<?> getNotices(@RequestParam(required = false, value = "page", defaultValue = "0") Integer pageNo) {

        Page<NoticeResDto> noticeResDtos = adminService.getNotices(pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(noticeResDtos), HttpStatus.OK);
    }

    // 공지사항 작성
    @PostMapping("/board/notice")
    public ResponseEntity<?> postNotice(@Valid @RequestBody NoticeReqDto noticeReqDto,
                                        @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {
        // 공지사항 작성
        Long userId = noticeReqDto.getUserId();
        String noticeTitle = noticeReqDto.getNoticeTitle();
        String noticeContents = noticeReqDto.getNoticeContents();
        Page<NoticeResDto> res = adminService.postNotice(userId, noticeTitle, noticeContents, pageNo);

        return new ResponseEntity<>(CMResDto.successDataRes(res), HttpStatus.OK);
    }

    // 공지사항 삭제
    @DeleteMapping("/board/notice/{noticeId}")
    public ResponseEntity<?> deleteNotice(@PathVariable Long noticeId) {
        adminService.deleteNoticeByNoticeId(noticeId);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    //마일리지 충전 요청/처리 내역 조회
    @GetMapping("/users/mileage-history")
    public ResponseEntity<?> getMileageRequests(@RequestParam(required = true, value = "status", defaultValue = "PROCESSING") String status,
                                                @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {

        Page<MileageCommonDto.MileageReqListDto> requests = adminService.getMileageRequests(status, pageNo);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(requests), HttpStatus.OK);
    }


    //마일리지 충전 요청 승인
    @PutMapping("/users/mileage/approval/{mileageReqId}")
    public ResponseEntity<?> approveMileageReq(@PathVariable(name = "mileageReqId") Long mileageReqId) {
        //true인 경우 승인, false인 경우 거부
        adminService.approveMileageReq(mileageReqId, true);
        log.info("데이터 변경 완료");
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    //마일리지 충전 요청 거부
    @PutMapping("/users/mileage/refusal/{mileageReqId}")
    public ResponseEntity<?> refusalMileageReq(@PathVariable(name = "mileageReqId") Long mileageReqId) {
        //true인 경우 승인, false인 경우 거부
        adminService.approveMileageReq(mileageReqId, false);
        log.info("데이터 변경 완료");
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // faq 조회
//    @GetMapping("/board/faq")
//    public ResponseEntity<?> getFaqs(@RequestParam(required = false, value = "page", defaultValue = "0") int pageNo,
//                                     @RequestParam(required = false, value = "type") FaqType faqType) {
//        Page<Faq> faqsPage = adminService.getAllFaqs(faqType, pageNo);
//        Page<FaqResDto.FaqListResDto> mappedFaqs = adminService.mapToFaqListResDto(faqsPage);
//
//        CMResDto<?> response = CMResDto.successDataRes(mappedFaqs);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

    @GetMapping("/board/faq")
    public ResponseEntity<?> getFaqs(@RequestParam(required = false, value = "page", defaultValue = "0") int pageNo,
                                     @RequestParam(required = false, value = "type") String type) {
        FaqType faqType = null;
        if (type != null) {
            faqType = FaqType.fromLabel(type);
            if (faqType == null) {
                throw new IllegalArgumentException("유효하지 않은 문의 유형: " + type);
            }
        }
        Page<Faq> faqsPage = adminService.getAllFaqs(FaqType.fromLabel(type), pageNo);
        Page<FaqResDto.FaqListResDto> mappedFaqs = adminService.mapToFaqListResDto(faqsPage);

        CMResDto<?> response = CMResDto.successDataRes(mappedFaqs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // faq 삭제
    @DeleteMapping("/board/faq/{faqId}")
    public ResponseEntity<?> deleteFaq(@PathVariable(name = "faqId") Long faqId) {
        adminService.deleteFaqByFaqId(faqId);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // FAQ 등록
    @PostMapping("/board/faq")
    public ResponseEntity<?> postFaq(@Valid @RequestBody FaqReqDto faqReqDto) {
        // FAQ 등록
        FaqType faqType = FaqType.fromLabel(faqReqDto.getFaqType());
        if (faqType == null) {
            throw new IllegalArgumentException("유효하지 않은 문의 유형: " + faqReqDto.getFaqType());
        }
        String faqQuestion = faqReqDto.getFaqTitle();
        String faqAnswer = faqReqDto.getFaqContents();
        Long faqId = adminService.postFaq(faqType, faqQuestion, faqAnswer);

        FaqResDto.FaqListResDto faqListResDto = new FaqResDto.FaqListResDto(faqId, faqType, faqQuestion, faqAnswer);
        return new ResponseEntity<>(CMResDto.successDataRes(faqListResDto), HttpStatus.OK);
    }


    // 상품 수정
    @PutMapping("/products/{productId}")
    public ResponseEntity<?> updateProduct(@Valid @RequestBody ProductReqDto productReqDto) {
        // 상품 수정
        adminService.updateProduct(productReqDto);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 상품 상세 정보 조회 api
    @GetMapping("/products/{productId}")
    public ResponseEntity<?> getProductInfo(@PathVariable Long productId) {
        ProductResDto.ProductInfoResDto res = adminService.getProductInfo(productId);
        return new ResponseEntity<>(CMResDto.successDataRes(res), HttpStatus.OK);
    }

    // 상품 리뷰
    @GetMapping("/products/review")
    public ResponseEntity<?> getReviews(@RequestParam(required = false, defaultValue = "0") Integer pageNo) {
        Page<AdminResDto.AdminReviewsResDto> adminReviewsResDtos = adminService.getProductReviews(pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(adminReviewsResDtos), HttpStatus.OK);
    }

    // 상품 QnA 전체 조회 api
    @GetMapping("/products/qna")
    public ResponseEntity<?> getQnAList(@RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {
        Page<QnaDto> qnaList = adminService.getQnaList(pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(qnaList), HttpStatus.OK);
    }

    // 상품 QnA 상세 + 답변 조회 api
    @GetMapping("/products/qna/{qnaId}")
    public ResponseEntity<?> getQnADetailed(@PathVariable Long qnaId) {
        QnaResDto.QnaDetailResDto qnaDetail = adminService.getQnADetail(qnaId);
        return new ResponseEntity<>(CMResDto.successDataRes(qnaDetail), HttpStatus.OK);
    }

    // 상품 QnA 답변 작성 api
    @PostMapping("/products/qna/{qnaId}")
    public ResponseEntity<?> writeQnaReply(@PathVariable Long qnaId, @Valid @RequestBody QnaReqDto.QnaReplyReqDto qnaReplyReqDto) {
        QnaResDto.QnaDetailResDto qnaDetail = adminService.createQnaReply(qnaId, qnaReplyReqDto.getQnaReplyContents());
        return new ResponseEntity<>(CMResDto.successDataRes(qnaDetail), HttpStatus.OK);
    }

    // 상품 QnA 답변 삭제 api
    @DeleteMapping("/products/qna/reply/{qnaReplyId}")
    public ResponseEntity<?> writeQnaReply(@PathVariable Long qnaReplyId) {
        QnaResDto.QnaDetailResDto qnaDetail = adminService.deleteQnaReply(qnaReplyId);
        return new ResponseEntity<>(CMResDto.successDataRes(qnaDetail), HttpStatus.OK);
    }

    // 반품 상태 변경
    @PutMapping("/orders/returns/{returnId}")
    public ResponseEntity<?> changeReturnStatus(@PathVariable Long returnId,
                                                @Valid @RequestBody ReturnReqDto.ChangeReturnStateDto ChangeReturnStateDto) {
        adminService.updateReturnState(returnId, ChangeReturnStateDto.getReturnStatus());
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 새로운 상품 추가
    @PostMapping("/product")
    public ResponseEntity<?> addNewProduct(@Valid @RequestBody ProductReqDto.ProductListDto productList) {
        adminService.saveProductList(productList);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 문의 목록 조회(카테고리별)
    @GetMapping("/board/inquiry")
    public ResponseEntity<?> getInquiries(@RequestParam(required = false, value = "type") String type,
                                          @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {
        InquiryType inquiryType = null;
        if (type != null) {
            inquiryType = InquiryType.fromLabel(type);
            if (inquiryType == null) {
                throw new IllegalArgumentException("유효하지 않은 문의 유형: " + type);
            }
        }
        Page<InquiryResDto.InquiryListResDto> mappedInquiries = adminService.getAllInquiriesByType(inquiryType, pageNo);

        CMResDto<?> response = CMResDto.successDataRes(mappedInquiries);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    // 문의 삭제
    @DeleteMapping("/board/inquiry/{inquiryId}")
    public ResponseEntity<?> deleteInquiry(@PathVariable Long inquiryId) {
        adminService.deleteInquiry(inquiryId);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }


    // 문의 답변 등록
    @PostMapping("/board/inquiry/reply/{inquiryId}")
    public ResponseEntity<?> postInquiryReply(@PathVariable Long inquiryId,
                                              @Valid @RequestBody InquiryReqDto.InquiryReplyRequestDto inquiryReplyRequestDto) {
        InquiryReply inquiryReply = InquiryReply.builder()
                .inquiryId(inquiryId)
                .inquiryReplyContents(inquiryReplyRequestDto.getInquiryReplyContents())
                .build();

        InquiryReply savedInquiryReply = adminService.createInquiryReply(inquiryReply);
        InquiryCommonDto.InquiryDetailsDto inquiryDetails = adminService.getInquiryDetails(savedInquiryReply.getInquiryReplyId());

        return new ResponseEntity<>(CMResDto.successDataRes(inquiryDetails), HttpStatus.OK);
    }

    // 문의 답변 삭제
    @DeleteMapping("/board/inquiry/reply/{inquiryReplyId}")
    public ResponseEntity<?> deleteInquiryReply(@PathVariable Long inquiryReplyId) {
        adminService.deleteInquiryReply(inquiryReplyId);

        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 배송 상태 변경
    @PutMapping("/orders/{detailId}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long detailId,
                                               @Valid @RequestBody OrderReqDto.OrderStatusReqDto requestDto) {
        String orderStatus = requestDto.getOrderStatus();
        adminService.updateOrderDetailState(detailId, orderStatus);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 상품 옵션 삭제
    @DeleteMapping("/products/{productId}/{optionId}")
    public ResponseEntity<?> deleteOption(@PathVariable(name = "productId") Long productId,
                                          @PathVariable(name = "optionId") Long optionId) {
        adminService.deleteOptionByOptionId(optionId);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 상품 목록 조회
    @GetMapping("/products/categories/{cateId}")
    public ResponseEntity<?> getProductsListAdmin(@PathVariable(name = "cateId") Long cateId,
                                                  @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {
        Page<ProductResDto.ProductListAdminResDto> productListAdminResDto = adminService.getProductListByCateogryId(cateId, pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(productListAdminResDto), HttpStatus.OK);
    }

    //상품 목록 카테고리별 검색 조회 api
    @GetMapping("/products/categories/{cateId}/search")
    public ResponseEntity<?> getSearchProductList(@PathVariable(name = "cateId") Long cateId,
                                                  @RequestParam(required = true, value = "q") String query,
                                                  @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo){
        Page<ProductResDto.ProductListAdminResDto> productListAdminResDto = adminService.getProductListBySearch(cateId, query, pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(productListAdminResDto), HttpStatus.OK);
    }

    // if:False -> 그룹별 관리자 조회
    // if:True -> 사원 검색
    @GetMapping("/admin-users")
    public ResponseEntity<?> getAdmins(@RequestParam(value = "q", required = false) Integer dktNum) {
        if (dktNum != null) {
            UserResDto.SearchUser searchUserRes = adminService.searchUser(dktNum);
            return new ResponseEntity<>(CMResDto.successDataRes(searchUserRes), HttpStatus.OK);
        } else {
            UserResDto.TotalAdminResDto adminUserResponse = adminService.getAdminUserDetails();
            return new ResponseEntity<>(CMResDto.successDataRes(adminUserResponse), HttpStatus.OK);
        }
    }

    // 권한 부여
    // 사용힌 ChangeRoleReqDto -> UserReqDto.ChangeRole로 변경
    @PutMapping("/admin-users/{userId}")
    public ResponseEntity<?> changeRole(@PathVariable Long userId,
                                        @Valid @RequestBody UserReqDto.ChangeRole newRole) {

        UserCommonDto.TokenResponseDto tokenResponseDto = adminService.changeRole(userId, newRole);
        return new ResponseEntity<>(CMResDto.successDataRes(tokenResponseDto), HttpStatus.OK);
    }

    // 상태별 반품 목록 조회
    @GetMapping("/orders/returns")
    public ResponseEntity<?> getReturnsList(@RequestParam(required = true, value = "status") String returnStatus,
                                            @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {

        ReturnResDto.ReturnListResDto returnListResDto = adminService.getReturns(returnStatus, pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(returnListResDto), HttpStatus.OK);
    }

    //마일리지 환불
    @PutMapping("/cancel-order-details")
    public ResponseEntity<?> putRefund(@Valid @RequestBody RefundReqDto RefundReqDto) {
        adminService.putRefund(RefundReqDto);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 주문 취소 목록 조회
    ///api/admin/cancel-order-details
    @GetMapping("/cancel-order-details")
    public ResponseEntity<?> getCancledOrder() {
        List<OrderResDto.OrderCancelResDto> orderCancleList = adminService.orderCancle();
        return new ResponseEntity<>(CMResDto.successDataRes(orderCancleList), HttpStatus.OK);
    }

    //재고 추가
    @PutMapping("/products/stock")
    public ResponseEntity<?> addProductStock(@RequestBody ProductReqDto.StockReqDto stockReqDto) {
        adminService.addProductStock(stockReqDto);
        Long productId = stockReqDto.getProductId();
        ProductResDto.ProductInfoOptionResDto productInfoOptionResDto = adminService.getProductInfoWithOption(productId);
        List<ProductResDto.ProductInfoOptionResDto> dtos = Collections.singletonList(productInfoOptionResDto);

        return new ResponseEntity<>(CMResDto.successDataRes(dtos), HttpStatus.OK);
    }

    // 배송 목록 조회
    @GetMapping("/orders")
    public ResponseEntity<?> getOrdersByStatus(@RequestParam String status,
                                               @RequestParam(required = false, defaultValue = "0") int pageNo) {
        OrderCommonDto.OrderDetailStateCountsDto statusCounts = adminService.getOrderDetailStateCounts();
        Page<OrderListAdminResDto> orderList = adminService.getOrdersByStatus(status, pageNo);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("confPayCount", statusCounts.getOrderCompleteCount());
        responseData.put("preShipCount", statusCounts.getDeliveryReadyCount());
        responseData.put("InTransitCount", statusCounts.getDeliveryIngCount());
        responseData.put("delivCompCount", statusCounts.getDeliveryCompleteCount());
        responseData.put("OrderCancelCount", statusCounts.getOrderCancelCount());
        responseData.put("ReturnRequestCount", statusCounts.getReturnRequestCount());
        responseData.put("ReturnCompleteCount", statusCounts.getReturnCompleteCount());
        responseData.put("orderList", orderList);

        return new ResponseEntity<>(CMResDto.successDataRes(responseData), HttpStatus.OK);
    }

    // 문의 내역 상세 조회
    @GetMapping("/board/inquiry/{inquiryId}")
    public ResponseEntity<?> getInquiryDetail(@PathVariable Long inquiryId) {
        InquiryResDto.InquiryDetailResDto inquiryDetail = adminService.getInquiryDetail(inquiryId);
        CMResDto<?> response = CMResDto.successDataRes(inquiryDetail);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


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
