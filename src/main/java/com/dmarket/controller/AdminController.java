package com.dmarket.controller;

import com.dmarket.constant.FaqType;
import com.dmarket.constant.InquiryType;
import com.dmarket.domain.board.Faq;
import com.dmarket.domain.board.InquiryReply;
import com.dmarket.dto.common.*;
import com.dmarket.dto.request.*;
import com.dmarket.dto.response.*;
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


    /**
     * 관리자: User
     */
    // 관리자 조회
    @GetMapping("/admin-users")
    public ResponseEntity<CMResDto<?>> getAdmins(@RequestParam(value = "q", required = false) Integer dktNum) {

        // 사원번호가 존재하면 해당 사원 검색
        if (dktNum != null) {
            UserResDto.SearchUser searchUserRes = adminService.searchUser(dktNum);
            return new ResponseEntity<>(CMResDto.successDataRes(searchUserRes), HttpStatus.OK);
        }

        // 사원번호가 존재하지 않으면 그룹별 관리자 조회
        UserResDto.TotalAdminResDto adminUserResponse = adminService.getAdminUserDetails();
        return new ResponseEntity<>(CMResDto.successDataRes(adminUserResponse), HttpStatus.OK);
    }

    // 권한 부여
    @PutMapping("/admin-users/{userId}")
    public ResponseEntity<CMResDto<UserCommonDto.TokenResponseDto>> changeRole(@PathVariable Long userId, @Valid @RequestBody UserReqDto.ChangeRole newRole) {
        // 사용한 ChangeRoleReqDto -> UserReqDto.ChangeRole로 변경
        UserCommonDto.TokenResponseDto tokenResponseDto = adminService.changeRole(userId, newRole);
        return new ResponseEntity<>(CMResDto.successDataRes(tokenResponseDto), HttpStatus.OK);
    }

    /**
     * 사용자: User
     */
    // 사용자 조회
    @GetMapping("/admin-user")
    public ResponseEntity<CMResDto<List<UserResDto.Search>>> getUsers(@RequestParam(value = "q", required = true) String email) {
        List<UserResDto.Search> userResDtos = adminService.getUsersFindByEmail(email);
        return new ResponseEntity<>(CMResDto.successDataRes(userResDtos), HttpStatus.OK);
    }

    // 사용자 삭제
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<CMResDto<String>> deleteUser(@PathVariable Long userId, HttpServletRequest request) {
        adminService.deleteUserByUserId(userId);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }


    /**
     * 마일리지 충전 요청: MileageReq
     */
    //마일리지 충전 요청/처리 내역 조회
    @GetMapping("/users/mileage-history")
    public ResponseEntity<CMResDto<Page<MileageCommonDto.MileageReqListDto>>> getMileageRequests(@RequestParam(required = true, value = "status", defaultValue = "PROCESSING") String status,
                                                @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {

        Page<MileageCommonDto.MileageReqListDto> requests = adminService.getMileageRequests(status, pageNo);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(requests), HttpStatus.OK);
    }

    //마일리지 충전 요청 승인
    @PutMapping("/users/mileage/approval/{mileageReqId}")
    public ResponseEntity<CMResDto<String>> approveMileageReq(@PathVariable(name = "mileageReqId") Long mileageReqId) {
        //true인 경우 승인, false인 경우 거부
        adminService.approveMileageReq(mileageReqId, true);
        log.info("데이터 변경 완료");
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    //마일리지 충전 요청 거부
    @PutMapping("/users/mileage/refusal/{mileageReqId}")
    public ResponseEntity<CMResDto<String>> refusalMileageReq(@PathVariable(name = "mileageReqId") Long mileageReqId) {
        //true인 경우 승인, false인 경우 거부
        adminService.approveMileageReq(mileageReqId, false);
        log.info("데이터 변경 완료");
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }


    /**
     * 공지사항: Notice
     */
    // 공지사항 목록 조회 with 페이지네이션
    @GetMapping("/board/notices")
    public ResponseEntity<CMResDto<Page<NoticeResDto>>> getNotices(@RequestParam(required = false, value = "page", defaultValue = "0") Integer pageNo) {

        Page<NoticeResDto> noticeResDtos = adminService.getNotices(pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(noticeResDtos), HttpStatus.OK);
    }

    // 공지사항 작성
    @PostMapping("/board/notice")
    public ResponseEntity<CMResDto<Page<NoticeResDto>>> postNotice(@Valid @RequestBody NoticeReqDto noticeReqDto,
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
    public ResponseEntity<CMResDto<String>> deleteNotice(@PathVariable Long noticeId) {
        adminService.deleteNoticeByNoticeId(noticeId);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }


    /**
     * 문의: Inquiry
     */
    // 문의 목록 조회(카테고리별)
    @GetMapping("/board/inquiry")
    public ResponseEntity<CMResDto<Page<InquiryResDto.InquiryListResDto>>> getInquiries(@RequestParam(required = false, value = "type") String type,
                                          @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {
        InquiryType inquiryType = null;
        if (type != null) {
            inquiryType = InquiryType.fromLabel(type);
            if (inquiryType == null) {
                throw new IllegalArgumentException("유효하지 않은 문의 유형: " + type);
            }
        }
        Page<InquiryResDto.InquiryListResDto> mappedInquiries = adminService.getAllInquiriesByType(inquiryType, pageNo);

        CMResDto<Page<InquiryResDto.InquiryListResDto>> response = CMResDto.successDataRes(mappedInquiries);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 문의 내역 상세 조회
    @GetMapping("/board/inquiry/{inquiryId}")
    public ResponseEntity<CMResDto<InquiryResDto.InquiryDetailResDto>> getInquiryDetail(@PathVariable Long inquiryId) {
        InquiryResDto.InquiryDetailResDto inquiryDetail = adminService.getInquiryDetail(inquiryId);
        CMResDto<InquiryResDto.InquiryDetailResDto> response = CMResDto.successDataRes(inquiryDetail);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 문의 삭제
    @DeleteMapping("/board/inquiry/{inquiryId}")
    public ResponseEntity<CMResDto<String>> deleteInquiry(@PathVariable Long inquiryId) {
        adminService.deleteInquiry(inquiryId);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }


    /**
     * 문의 답변: InquiryReply
     */
    // 문의 답변 등록
    @PostMapping("/board/inquiry/reply/{inquiryId}")
    public ResponseEntity<CMResDto<InquiryCommonDto.InquiryDetailsDto>> postInquiryReply(@PathVariable Long inquiryId,
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
    public ResponseEntity<CMResDto<String>> deleteInquiryReply(@PathVariable Long inquiryReplyId) {
        adminService.deleteInquiryReply(inquiryReplyId);

        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }


    /**
     * FAQ
     */
    // FAQ 조회
    @GetMapping("/board/faq")
    public ResponseEntity<CMResDto<Page<FaqResDto.FaqListResDto>>> getFaqs(@RequestParam(required = false, value = "page", defaultValue = "0") int pageNo,
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

        CMResDto<Page<FaqResDto.FaqListResDto>> response = CMResDto.successDataRes(mappedFaqs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // FAQ 등록
    @PostMapping("/board/faq")
    public ResponseEntity<CMResDto<FaqResDto.FaqListResDto>> postFaq(@Valid @RequestBody FaqReqDto faqReqDto) {
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

    // FAQ 삭제
    @DeleteMapping("/board/faq/{faqId}")
    public ResponseEntity<CMResDto<String>> deleteFaq(@PathVariable(name = "faqId") Long faqId) {
        adminService.deleteFaqByFaqId(faqId);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }


    /**
     * 상품: Product
     */
    // 상품 목록 조회
    @GetMapping("/products/categories/{cateId}")
    public ResponseEntity<CMResDto<ProductResDto.ProductListAdminResDto>> getProductsListAdmin(@PathVariable(name = "cateId") Long cateId,
                                                  @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {
        ProductResDto.ProductListAdminResDto resDto = adminService.getProductListByCategoryId(cateId, pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(resDto), HttpStatus.OK);
    }

    //상품 목록 카테고리별 검색 조회 api
    @GetMapping("/products/categories/{cateId}/search")
    public ResponseEntity<CMResDto<ProductResDto.ProductListAdminResDto>> getSearchProductList(@PathVariable(name = "cateId") Long cateId,
                                                  @RequestParam(required = true, value = "q") String query,
                                                  @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo){
        ProductResDto.ProductListAdminResDto resDto = adminService.getProductListBySearch(cateId, query, pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(resDto), HttpStatus.OK);
    }

    // 상품 상세 정보 조회 api
    @GetMapping("/products/{productId}")
    public ResponseEntity<CMResDto<ProductResDto.ProductInfoResDto>> getProductInfo(@PathVariable Long productId) {
        ProductResDto.ProductInfoResDto res = adminService.getProductInfo(productId);
        return new ResponseEntity<>(CMResDto.successDataRes(res), HttpStatus.OK);
    }

    // 새로운 상품 추가
    @PostMapping("/product")
    public ResponseEntity<CMResDto<String>> addNewProduct(@Valid @RequestBody ProductReqDto.ProductListDto productList) {
        adminService.saveProductList(productList);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 상품 수정
    @PutMapping("/products/{productId}")
    public ResponseEntity<CMResDto<String>> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductReqDto productReqDto) {
        // 상품 수정
        productReqDto.setProductId(productId);
        adminService.updateProduct(productReqDto);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    //재고 추가
    @PutMapping("/products/stock")
    public ResponseEntity<CMResDto<List<ProductResDto.ProductInfoOptionResDto>>> addProductStock(@RequestBody ProductReqDto.StockReqDto stockReqDto) {
        adminService.addProductStock(stockReqDto);
        Long productId = stockReqDto.getProductId();
        ProductResDto.ProductInfoOptionResDto productInfoOptionResDto = adminService.getProductInfoWithOption(productId);
        List<ProductResDto.ProductInfoOptionResDto> dtos = Collections.singletonList(productInfoOptionResDto);

        return new ResponseEntity<>(CMResDto.successDataRes(dtos), HttpStatus.OK);
    }

    /**
     * 상품 옵션: ProductOption
     */
    // 상품 옵션 삭제
    @DeleteMapping("/products/{productId}/{optionId}")
    public ResponseEntity<CMResDto<String>> deleteOption(@PathVariable(name = "productId") Long productId,
                                          @PathVariable(name = "optionId") Long optionId) {
        adminService.deleteOptionByOptionId(optionId);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }


    /**
     * 상품 리뷰: ProductReview
     */
    // 상품 리뷰
    @GetMapping("/products/review")
    public ResponseEntity<CMResDto<Page<AdminResDto.AdminReviewsResDto>>> getReviews(@RequestParam(required = false, defaultValue = "0") Integer pageNo) {
        Page<AdminResDto.AdminReviewsResDto> adminReviewsResDtos = adminService.getProductReviews(pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(adminReviewsResDtos), HttpStatus.OK);
    }


    /**
     * 상품 QnA: Qna
     */
    // 상품 QnA 전체 조회 api
    @GetMapping("/products/qna")
    public ResponseEntity<CMResDto<Page<QnaDto>>> getQnAList(@RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {
        Page<QnaDto> qnaList = adminService.getQnaList(pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(qnaList), HttpStatus.OK);
    }

    // 상품 QnA 상세 + 답변 조회 api
    @GetMapping("/products/qna/{qnaId}")
    public ResponseEntity<CMResDto<QnaResDto.QnaDetailResDto>> getQnADetailed(@PathVariable Long qnaId) {
        QnaResDto.QnaDetailResDto qnaDetail = adminService.getQnADetail(qnaId);
        return new ResponseEntity<>(CMResDto.successDataRes(qnaDetail), HttpStatus.OK);
    }

    // 상품 QnA 답변 작성 api
    @PostMapping("/products/qna/{qnaId}")
    public ResponseEntity<CMResDto<QnaResDto.QnaDetailResDto>> writeQnaReply(@PathVariable Long qnaId, @Valid @RequestBody QnaReqDto.QnaReplyReqDto qnaReplyReqDto) {
        QnaResDto.QnaDetailResDto qnaDetail = adminService.createQnaReply(qnaId, qnaReplyReqDto.getQnaReplyContents());
        return new ResponseEntity<>(CMResDto.successDataRes(qnaDetail), HttpStatus.OK);
    }

    // 상품 QnA 답변 삭제 api
    @DeleteMapping("/products/qna/reply/{qnaReplyId}")
    public ResponseEntity<CMResDto<QnaResDto.QnaDetailResDto>> writeQnaReply(@PathVariable Long qnaReplyId) {
        QnaResDto.QnaDetailResDto qnaDetail = adminService.deleteQnaReply(qnaReplyId);
        return new ResponseEntity<>(CMResDto.successDataRes(qnaDetail), HttpStatus.OK);
    }


    /**
     * 주문: Order
     */
    // 배송 목록 조회
    @GetMapping("/orders")
    public ResponseEntity<CMResDto<Map<String, Object>>> getOrdersByStatus(@RequestParam String status,
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

    // 배송 상태 변경
    @PutMapping("/orders/{detailId}")
    public ResponseEntity<CMResDto<String>> updateOrderStatus(@PathVariable Long detailId,
                                               @Valid @RequestBody OrderReqDto.OrderStatusReqDto requestDto) {
        String orderStatus = requestDto.getOrderStatus();
        adminService.updateOrderDetailState(detailId, orderStatus);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }


    /**
     * 주문 취소, 환불: Refund
     */
    // 주문 취소 -> 마일리지 환불
    @PutMapping("/cancel-order-details")
    public ResponseEntity<CMResDto<String>> putRefund(@Valid @RequestBody RefundReqDto refundReqDto) {
        adminService.putRefund(refundReqDto);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 주문 취소 목록 조회
    @GetMapping("/cancel-order-details")
    public ResponseEntity<CMResDto<Page<OrderResDto.OrderCancelResDto>>> getCanceledOrder(@RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {
        Page<OrderResDto.OrderCancelResDto> orderCancleList = adminService.orderCancle(pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(orderCancleList), HttpStatus.OK);
    }


    /**
     * 반품: Returns
     */
    // 상태별 반품 목록 조회
    @GetMapping("/orders/returns")
    public ResponseEntity<CMResDto<ReturnResDto.ReturnListResDto>> getReturnsList(@RequestParam(required = true, value = "status") String returnStatus,
                                            @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {

        ReturnResDto.ReturnListResDto returnListResDto = adminService.getReturns(returnStatus, pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(returnListResDto), HttpStatus.OK);
    }

    // 반품 상태 변경
    @PutMapping("/orders/returns/{returnId}")
    public ResponseEntity<CMResDto<String>> changeReturnStatus(@PathVariable Long returnId,
                                                @Valid @RequestBody ReturnReqDto.ChangeReturnStateDto changeReturnStateDto) {
        adminService.updateReturnState(returnId, changeReturnStateDto.getReturnStatus());
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

}
