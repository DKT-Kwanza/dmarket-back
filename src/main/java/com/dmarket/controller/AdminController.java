package com.dmarket.controller;

import com.dmarket.constant.FaqType;
import com.dmarket.constant.InquiryType;
import com.dmarket.domain.board.Faq;
import com.dmarket.domain.board.InquiryReply;
import com.dmarket.dto.common.InquiryDetailsDto;
import com.dmarket.dto.request.*;
import com.dmarket.dto.response.*;
import com.dmarket.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    // 페이지 번호 유효성 검사 메소드
    public int pageVaildation(int page){
        page = page > 0 ? page - 1 : page;

        return page;
    }

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

    // 사용자 삭제
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        adminService.deleteUserByUserId(userId);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 사용자 조회
    @GetMapping("/admin-user")
    public ResponseEntity<?> getUsers(@RequestParam(value = "q", required = true) Integer dktNum) {
        List<UserResDto> userResDtos = adminService.getUsersFindByDktNum(dktNum);
        return new ResponseEntity<>(CMResDto.successDataRes(userResDtos), HttpStatus.OK);
    }

    // 공지사항 목록 조회 with 페이지네이션
    @GetMapping("/board/notices")
    public ResponseEntity<?> getNotices(@RequestParam(required = false, defaultValue = "0") Integer page) {
        page = pageVaildation(page);

        Page<NoticeResDto> noticeResDtos = adminService.getNotices(page);
        return new ResponseEntity<>(CMResDto.successDataRes(noticeResDtos), HttpStatus.OK);
    }

    // 공지사항 작성
    @PostMapping("/board/notice")
    public ResponseEntity<?> postNotice(@Valid @RequestBody NoticeReqDto noticeReqDto,
                                        @RequestParam(required = false, value = "page", defaultValue = "0") int page) {
        page = pageVaildation(page);

        // 공지사항 작성
        Long userId = noticeReqDto.getUserId();
        String noticeTitle = noticeReqDto.getNoticeTitle();
        String noticeContents = noticeReqDto.getNoticeContents();
        Page<NoticeResDto> res =  adminService.postNotice(userId, noticeTitle, noticeContents, page);

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
                                                @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo){
        pageNo = pageVaildation(pageNo);
        MileageReqListResDto requests = adminService.getMileageRequests(status, pageNo);

        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(requests), HttpStatus.OK);
    }


    //마일리지 충전 요청 승인
    @PutMapping("/users/mileage/approval/{mileageReqId}")
    public ResponseEntity<?> approveMileageReq(@PathVariable(name = "mileageReqId") Long mileageReqId){
        //true인 경우 승인, false인 경우 거부
        adminService.approveMileageReq(mileageReqId, true);
        log.info("데이터 변경 완료");
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    //마일리지 충전 요청 거부
    @PutMapping("/users/mileage/refusal/{mileageReqId}")
    public ResponseEntity<?> refusalMileageReq(@PathVariable(name = "mileageReqId") Long mileageReqId){
        //true인 경우 승인, false인 경우 거부
        adminService.approveMileageReq(mileageReqId, false);
        log.info("데이터 변경 완료");
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);

    }

    // faq 조회
    @GetMapping("/board/faq")
    public ResponseEntity<?> getFaqs(@RequestParam(required = false, value = "page", defaultValue = "0") int pageNo,
                                     @RequestParam(required = false, value = "type") FaqType faqType) {
        pageNo = pageVaildation(pageNo);

        Page<Faq> faqsPage = adminService.getAllFaqs(faqType, pageNo);
        Page<FaqListResDto> mappedFaqs = adminService.mapToFaqListResDto(faqsPage);

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
        FaqType faqType = faqReqDto.getFaqType();
        String faqQuestion = faqReqDto.getFaqTitle();
        String faqAnswer = faqReqDto.getFaqContents();
        Long faqId = adminService.postFaq(faqType, faqQuestion, faqAnswer);

        FaqListResDto faqListResDto = new FaqListResDto(faqId, faqType, faqQuestion, faqAnswer);

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
        Long userId = 1L;
        ProductInfoResDto res = adminService.getProductInfo(productId, userId);
        return new ResponseEntity<>(CMResDto.successDataRes(res), HttpStatus.OK);
    }

    // 상품 리뷰
    @GetMapping("/products/review")
    public ResponseEntity<?> getReviews(@RequestParam(required = false, defaultValue = "0") Integer page) {
        page = pageVaildation(page);

        Page<AdminReviewsResDto> adminReviewsResDtos = adminService.getProductReviews(page);
        return new ResponseEntity<>(CMResDto.successDataRes(adminReviewsResDtos), HttpStatus.OK);
    }

    // 상품 QnA 전체 조회 api
    @GetMapping("/products/qna")
    public ResponseEntity<?> getQnAList(@RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {
        pageNo = pageVaildation(pageNo);

        QnaListResDto qnaList = adminService.getQnaList(pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(qnaList), HttpStatus.OK);
    }

    // 상품 QnA 상세 + 답변 조회 api
    @GetMapping("/products/qna/{qnaId}")
    public ResponseEntity<?> getQnADetailed(@PathVariable Long qnaId) {
        QnaDetailResDto qnaDetail = adminService.getQnADetail(qnaId);
        return new ResponseEntity<>(CMResDto.successDataRes(qnaDetail), HttpStatus.OK);
    }

    // 상품 QnA 답변 작성 api
    @PostMapping("/products/qna/{qnaId}")
    public ResponseEntity<?> writeQnaReply(@PathVariable Long qnaId, @Valid @RequestBody QnaReplyReqDto qnaReplyReqDto){
            QnaDetailResDto qnaDetail = adminService.createQnaReply(qnaId, qnaReplyReqDto.getQnaReplyContents());
            return new ResponseEntity<>(CMResDto.successDataRes(qnaDetail), HttpStatus.OK);
    }

    // 상품 QnA 답변 삭제 api
    @DeleteMapping("/products/qna/reply/{qnaReplyId}")
    public ResponseEntity<?> writeQnaReply(@PathVariable Long qnaReplyId){
        QnaDetailResDto qnaDetail = adminService.deleteQnaReply(qnaReplyId);
        return new ResponseEntity<>(CMResDto.successDataRes(qnaDetail), HttpStatus.OK);
    }

    // 반품 상태 변경
    @PutMapping("/orders/returns/{returnId}")
    public ResponseEntity<?> changeReturnStatus(@PathVariable Long returnId, @Valid @RequestBody ChangeReturnStateDto changeReturnStateDto) {
        String returnState = changeReturnStateDto.getReturnStatus();
        adminService.updateReturnState(returnId, returnState);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 새로운 상품 추가
    @PostMapping("/product")
    public ResponseEntity<?> addNewProduct(@Valid @RequestBody List<ProductListDto> productList) {
        adminService.saveProductList(productList);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 문의 목록 조회(카테고리별)
    @GetMapping("/board/inquiry")
    public ResponseEntity<?> getInquiries(@RequestParam(required = false, value = "type") InquiryType inquiryType,
                                          @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {
        pageNo = pageVaildation(pageNo);

        Page<InquiryListResDto> mappedInquiries = adminService.getAllInquiriesByType(inquiryType, pageNo);

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
                                              @Valid @RequestBody InquiryReplyRequestDto inquiryReplyRequestDto) {
        InquiryReply inquiryReply = InquiryReply.builder()
                .inquiryId(inquiryId)
                .inquiryReplyContents(inquiryReplyRequestDto.getInquiryReplyContents())
                .build();

        InquiryReply createdInquiryReply = adminService.createInquiryReply(inquiryReply);

        InquiryDetailsDto inquiryDetails = adminService.getInquiryDetails(inquiryId);

        return new ResponseEntity<>(CMResDto.successDataRes(inquiryDetails), HttpStatus.OK);
    }

    // 문의 답변 삭제
    @DeleteMapping("/board/inquiry/reply/{inquiryReplyId}")
    public ResponseEntity<?> deleteInquiryReply(@PathVariable Long inquiryReplyId) {
        adminService.deleteInquiryReply(inquiryReplyId);

        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 배송 상태 변경경
    @PutMapping("/orders/{detailId}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long detailId,
                                               @Valid @RequestBody OrderStatusReqDto requestDto) {
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
    @GetMapping("/products/categories/{categoryId}")
    public ResponseEntity<?> getProductsListAdmin(@PathVariable(name = "categoryId") Long categoryId) {
        List<ProductListAdminResDto> productListAdminResDto = adminService.getProductListByCateogryId(categoryId);
        return new ResponseEntity<>(CMResDto.successDataRes(productListAdminResDto), HttpStatus.OK);
    }

    // if:False -> 그룹별 관리자 조회
    // if:True -> 사원 검색
    @GetMapping("/admin-users")
    public ResponseEntity<?> getAdmins(@RequestParam(value = "q", required = false) Integer dktNum) {
        if (dktNum != null){
            SearchUserResDto searchUserRes = adminService.searchUser(dktNum);
            return new ResponseEntity<>(CMResDto.successDataRes(searchUserRes), HttpStatus.OK);
        }else {
            TotalAdminResDto adminUserResponse = adminService.getAdminUserDetails();
            return new ResponseEntity<>(CMResDto.successDataRes(adminUserResponse), HttpStatus.OK);
        }
    }

    // 권한 부여한
    // 사용힌 ChangeRoleReqDto
    @PutMapping("/admin-users/{userId}")
    public ResponseEntity<?> changeRole(@PathVariable Long userId,
                                        @Valid @RequestBody ChangeRoleReqDto newRole){
        adminService.changeRole(userId,newRole);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 상태별 반품 목록 조회
    @GetMapping("/orders/returns")
    public ResponseEntity<?> getReturnsList(@RequestParam(required = true, value = "status") String returnStatus,
                                            @RequestParam(required = false, defaultValue = "0") int page) {
        page = pageVaildation(page);
        ReturnListResDto returnListResDto = adminService.getReturns(returnStatus, page);

        return new ResponseEntity<>(CMResDto.successDataRes(returnListResDto), HttpStatus.OK);
    }

    //마일리지 환불
    @PutMapping("/cancel-order-details")
    public ResponseEntity<?> putRefund(@Valid @RequestBody RefundReqDto RefundReqDto) {
            adminService.putRefund(RefundReqDto);
            return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    ///api/admin/cancel-order-details
    @GetMapping("/cancel-order-details")
    public ResponseEntity<?> getCancledOrder(){
            List<OrderCancelResDto> orderCancleList = adminService.orderCancle();
            return new ResponseEntity<>(CMResDto.successDataRes(orderCancleList), HttpStatus.OK);
    }

}
