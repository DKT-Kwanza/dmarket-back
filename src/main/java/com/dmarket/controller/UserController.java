package com.dmarket.controller;

import com.dmarket.constant.InquiryType;
import com.dmarket.domain.board.Inquiry;
import com.dmarket.domain.order.Order;
import com.dmarket.domain.user.User;
import com.dmarket.dto.common.CartCommonDto;
import com.dmarket.dto.common.InquiryRequestDto;
import com.dmarket.dto.common.MileageCommonDto;
import com.dmarket.dto.request.*;
import com.dmarket.dto.response.*;
import com.dmarket.exception.ErrorCode;
import com.dmarket.jwt.JWTUtil;
import com.dmarket.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JWTUtil jwtUtil;

    //회원가입
    @PostMapping("/join")
    public ResponseEntity<CMResDto<String>> join(@Valid @RequestBody UserReqDto.Join dto) {

        userService.verifyJoin(dto);
        Long userId = userService.join(dto);
        return new ResponseEntity<>(CMResDto.successDataRes("userId=" + userId), HttpStatus.OK);
    }

    // 이메일 인증 코드 전송
    @PostMapping("/email")
    public ResponseEntity<CMResDto<String>> email(@Valid @RequestBody String userEmail) {
        userService.sendCodeToEmail(userEmail);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    //이메일 인증 코드 확인
    @PostMapping("/email/verify")
    public ResponseEntity<CMResDto<String>> emailVerify(@Valid @RequestBody UserReqDto.Emails dto) {
        userService.isValidEmailCode(dto.getUserEmail(), dto.getCode());
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 장바구니 추가 api
    @PostMapping("/{userId}/cart")
    public ResponseEntity<CMResDto<String>> addCart(@PathVariable Long userId, @Valid @RequestBody CartReqDto.AddCartReqDto addCartReqDto, HttpServletRequest request) {
        ResponseEntity<CMResDto<String>> authorization = checkAuthorization(userId, request);
        if(authorization != null){
            return authorization;
        }
        Long productId = addCartReqDto.getProductId();
        Long optionId = addCartReqDto.getOptionId();
        Integer productCount = addCartReqDto.getProductCount();
        userService.addCart(userId, productId, optionId, productCount);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 위시리스트 추가 api
    @PostMapping("/{userId}/wish")
    public ResponseEntity<CMResDto<String>> addWish(@PathVariable Long userId, @Valid @RequestBody WishListReqDto.AddWishReqDto addWishReqDto, HttpServletRequest request) {
        ResponseEntity<CMResDto<String>> authorization = checkAuthorization(userId, request);
        if(authorization != null){
            return authorization;
        }
        // 위시리스트 추가
        Long productId = addWishReqDto.getProductId();
        userService.addWish(userId, productId);

        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 위시리스트에 담긴 상품인지 확인 api
    @GetMapping("/{userId}/wish/{productId}")
    public ResponseEntity<CMResDto<WishResDto.IsWishResDto>> checkIsWish(@PathVariable Long userId,
                                         @PathVariable Long productId){
        WishResDto.IsWishResDto isWishResDto = userService.checkIsWish(userId, productId);
        return new ResponseEntity<>(CMResDto.successDataRes(isWishResDto), HttpStatus.OK);
    }

    // 위시리스트 조회
    @GetMapping("/{userId}/wish")
    public ResponseEntity<?> getWishlistByUserId(@PathVariable(name = "userId") Long userId,
                                                 @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo, HttpServletRequest request) {
        ResponseEntity<CMResDto<String>> authorization = checkAuthorization(userId, request);
        if(authorization != null){
            return authorization;
        }
        WishResDto.WishlistResDto wishlist = userService.getWishlistByUserId(userId,pageNo);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(wishlist), HttpStatus.OK);
    }

    // 장바구니 상품 개수 조회
    @GetMapping("{userId}/cart-count")
    public ResponseEntity<?> getCartCount(@PathVariable(name = "userId") Long userId, HttpServletRequest request) {
        System.out.println("userId = " + userId);
        ResponseEntity<CMResDto<String>> authorization = checkAuthorization(userId, request);
        if(authorization != null){
            return authorization;
        }

        CartResDto.CartCountResDto cartCount = userService.getCartCount(userId);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(cartCount), HttpStatus.OK);
    }

    // 마이페이지 서브헤더 사용자 정보 및 마일리지 조회
    @GetMapping("/{userId}/mypage/mileage")
    public ResponseEntity<?> getSubHeader(@PathVariable(name = "userId") Long userId, HttpServletRequest request) {
        ResponseEntity<CMResDto<String>> authorization = checkAuthorization(userId, request);
        if(authorization != null){
            return authorization;
        }
        UserResDto.UserHeaderInfo subHeader = userService.getSubHeader(userId);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(subHeader), HttpStatus.OK);
    }

    // 위시리스트 삭제
    @DeleteMapping("/{userId}/wish/{wishlistIds}")
    public ResponseEntity<CMResDto<String>> deleteWishlistId(@PathVariable(name = "userId") Long userId,
                                              @PathVariable(name = "wishlistIds") List<Long> wishlistIds, HttpServletRequest request) {
        ResponseEntity<CMResDto<String>> authorization = checkAuthorization(userId, request);
        if(authorization != null){
            return authorization;
        }
        for (Long wishlistId : wishlistIds) {
            userService.deleteWishlistById(wishlistId);
        }
        log.info("데이터 삭제 완료");
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 사용자 정보 조회
    @GetMapping("/{userId}/mypage/myinfo")
    public ResponseEntity<?> getUserInfoByUserId(HttpServletRequest request,
                                                 @PathVariable(name = "userId") Long userId) {
        ResponseEntity<CMResDto<String>> authorization = checkAuthorization(userId, request);
        if(authorization != null){
            return authorization;
        }
        request.getHeader("Authorization");
        UserResDto.UserInfo userInfo = userService.getUserInfoByUserId(userId);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(userInfo), HttpStatus.OK);
    }


    // 사용자 비밀번호 변경
    @PutMapping("{userId}/mypage/change-pwd")
    public ResponseEntity<CMResDto<String>> updatePassword(HttpServletRequest request,
                                            @PathVariable(name = "userId") Long userId,
                                            @Valid @RequestBody UserReqDto.ChangePwd changePwdReqDto) {
        ResponseEntity<CMResDto<String>> authorization = checkAuthorization(userId, request);
        if(authorization != null){
            return authorization;
        }
        String currentPassword = changePwdReqDto.getCurrentPassword();
        String newPassword = changePwdReqDto.getNewPassword();

        User user = userService.validatePassword(request, currentPassword);
        userService.updatePassword(newPassword, user);

        log.info("데이터 변경 완료");
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 사용자 배송지 수정
    @PutMapping("{userId}/mypage/myinfo")
    public ResponseEntity<?> updateAddress(HttpServletRequest request,
                                           @PathVariable(name = "userId") Long userId,
                                           @Valid @RequestBody UserReqDto.UserAddress userAddressReqDto) {
        ResponseEntity<CMResDto<String>> authorization = checkAuthorization(userId, request);
        if(authorization != null){
            return authorization;
        }
        UserResDto.UserAddress result = userService.updateAddress(request, userId, userAddressReqDto);
        log.info("데이터 변경 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(result), HttpStatus.OK);
    }


    // 장바구니 조회
    @GetMapping("/{userId}/cart")
    public ResponseEntity<?> getCarts(@PathVariable Long userId, HttpServletRequest request) {
        System.out.println("test.A");
        System.out.println("userId = " + userId);
        System.out.println("request = " + request);
        // 권한 검증 메서드
        ResponseEntity<CMResDto<String>> authorization = checkAuthorization(userId, request);
        if(authorization != null){
            System.out.println("test.B");
            return authorization;
        }
        System.out.println("test.C");
        List<CartCommonDto.CartListDto> cartListDtos = userService.getCartsFindByUserId(userId);
        CartResDto.TotalCartResDto totalCartResDto = new CartResDto.TotalCartResDto(cartListDtos);
        return new ResponseEntity<>(CMResDto.successDataRes(totalCartResDto), HttpStatus.OK);
    }


    // 장바구니 삭제
    @DeleteMapping("/{userId}/cart/{cartIds}")
    public ResponseEntity<CMResDto<String>> deleteCart(@PathVariable Long userId,
                                        @PathVariable(name = "cartIds") List<Long> cartIds, HttpServletRequest request) {

        ResponseEntity<CMResDto<String>> authorization = checkAuthorization(userId, request);
        if(authorization != null){
            return authorization;
        }

        for (Long cartId : cartIds) {
            userService.deleteCartByCartId(cartId);
            log.info("장바구니 삭제 완료: cartId={}", cartId);
        }
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 작성한 Qna 조회
    @GetMapping("/{userId}/mypage/qna")
    public ResponseEntity<?> getQna(@PathVariable Long userId,
                                    @RequestParam(required = false, value = "page", defaultValue = "0") Integer pageNo, HttpServletRequest request) {
        ResponseEntity<CMResDto<String>> authorization = checkAuthorization(userId, request);
        if(authorization != null){
            return authorization;
        }
        Page<QnaResDto.QnaTotalListResDto> qnaListResDtos = userService.getQnasfindByUserId(userId, pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(qnaListResDtos), HttpStatus.OK);
    }

    // 리뷰 작성 가능한 상품 목록 조회
    @GetMapping("/{userId}/mypage/available-reviews")
    public ResponseEntity<?> getAvailableReviews(@PathVariable Long userId,
                                                 @RequestParam(required = false, value = "page", defaultValue = "0") Integer pageNo , HttpServletRequest request) {
        ResponseEntity<CMResDto<String>> authorization = checkAuthorization(userId, request);
        if(authorization != null){
            return authorization;
        }
        Page<OrderResDto<OrderDetailResDto>> orderResDtos = userService.getOrderDetailsWithoutReviewByUserId(userId, pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(orderResDtos), HttpStatus.OK);
    }

    // 작성한 리뷰 목록 조회
    @GetMapping("/{userId}/mypage/written-reviews")
    public ResponseEntity<?> getWrittenReviews(@PathVariable Long userId,
                                               @RequestParam(required = false, value = "page", defaultValue = "0") Integer pageNo,HttpServletRequest request) {
        ResponseEntity<CMResDto<String>> authorization = checkAuthorization(userId, request);
        if(authorization != null){
            return authorization;
        }
        Page<OrderResDto<ReviewResDto>> orderResDtos = userService.getOrderDetailsWithReviewByUserId(userId, pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(orderResDtos), HttpStatus.OK);
    }

    // 문의 작성
    @PostMapping("/{userId}/board/inquiry")
    public ResponseEntity<CMResDto<String>> createInquiry(@PathVariable Long userId,
                                                     @Valid @RequestBody InquiryRequestDto inquiryRequestDto, HttpServletRequest request) {

        InquiryType inquiryType = InquiryType.fromLabel(inquiryRequestDto.getInquiryType());
        if (inquiryType == null) {
            throw new IllegalArgumentException("유효하지 않은 문의 유형: " + inquiryRequestDto.getInquiryType());
        }

        Inquiry inquiry = Inquiry.builder()
                .userId(userId)
                .inquiryType(inquiryType)
                .inquiryTitle(inquiryRequestDto.getInquiryTitle())
                .inquiryContents(inquiryRequestDto.getInquiryContents())
                .inquiryImg(inquiryRequestDto.getInquiryImg())
                .inquiryState(false) // 기본값 false로 설정
                .build();
        userService.createInquiry(inquiry);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }


    // 마일리지 사용(충전) 내역 api
    @GetMapping("/{userId}/mypage/mileage-usage")
    public ResponseEntity<?> getMileageUsage(@PathVariable Long userId,
                                             @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo, HttpServletRequest request) {
        ResponseEntity<CMResDto<String>> authorization = checkAuthorization(userId, request);
        if(authorization != null){
            return authorization;
        }

        // 충전 요청
        Page<MileageCommonDto.MileageDto> res = userService.getMileageUsage(userId, pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(res), HttpStatus.OK);
    }

    // 마일리지 충전 요청 api
    @PostMapping("/{userId}/mypage/mileage-charge")
    public ResponseEntity<CMResDto<String>> mileageChargeReq(@PathVariable Long userId,
                                              @Valid @RequestBody MileageReqDto.MileageChargeReqDto mileageChargeReqDto, HttpServletRequest request) {
        ResponseEntity<CMResDto<String>> authorization = checkAuthorization(userId, request);
        if(authorization != null){
            return authorization;
        }

        // 충전 요청
        userService.mileageChargeReq(userId, mileageChargeReqDto.getMileageCharge());
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 작성한 고객 문의 목록
    @GetMapping("/{userId}/mypage/inquiry")
    public ResponseEntity<?> getUserInquiryAllByUserId(@PathVariable(name = "userId") Long userId,
                                                       @RequestParam(required = false, value = "page", defaultValue = "0") Integer pageNo, HttpServletRequest request) {
        ResponseEntity<CMResDto<String>> authorization = checkAuthorization(userId, request);
        if(authorization != null){
            return authorization;
        }
        Page<InquiryResDto.UserInquiryAllResDto> userInquiryAllResDtos = userService.getUserInquiryAllbyUserId(userId, pageNo);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(userInquiryAllResDtos), HttpStatus.OK);
    }

    // 사용자 주문 내역 상세 조회 USER-031
    @GetMapping("/{userId}/mypage/orders/{orderId}")
    public ResponseEntity<?> getUserOrderDetailListByOrderId(@PathVariable(name = "userId") Long userId,
                                                             @PathVariable(name = "orderId") Long orderId,
                                                             @RequestParam(required = false, value = "page", defaultValue = "0") Integer pageNo,HttpServletRequest request) {

        ResponseEntity<CMResDto<String>> authorization = checkAuthorization(userId, request);
        if(authorization != null){
            return authorization;
        }
        OrderResDto.OrderDetailListResDto userOrderDetailResDtos = userService.getOrderDetailListByOrderId(userId, orderId, pageNo);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(userOrderDetailResDtos), HttpStatus.OK);
    }

    // 주문 / 배송 내역 조회 : USER-030
    @GetMapping("/{userId}/mypage/orders")
    public ResponseEntity<?> getUserOrderList(@PathVariable(name = "userId") Long userId,
                                              @RequestParam(required = false, value = "page", defaultValue = "0") Integer pageNo,HttpServletRequest request) {
        ResponseEntity<CMResDto<String>> authorization = checkAuthorization(userId, request);
        if(authorization != null){
            return authorization;
        }
        OrderResDto.OrderListResDto userOrderListResDtos = userService.getOrderListResByUserId(userId, pageNo);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(userOrderListResDtos), HttpStatus.OK);
    }

    // 주문 취소 요청
    @PostMapping("/{userId}/mypage/order/cancel")
    public ResponseEntity<?> postOrderCancel(@PathVariable(name = "userId") Long userId,
                                             @Valid @RequestBody OrderCancelReqDto orderCancelReqDto,
                                             @RequestParam(required = false, value = "page", defaultValue = "0") Integer pageNo,HttpServletRequest request) {

        ResponseEntity<CMResDto<String>> authorization = checkAuthorization(userId, request);
        if(authorization != null){
            return authorization;
        }
        OrderResDto.OrderDetailListResDto orderDetailListResDto = userService.postOrderCancel(orderCancelReqDto.getOrderId(), orderCancelReqDto.getOrderDetailId(), userId, pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(orderDetailListResDto), HttpStatus.OK);

    }

    // 반품 요청(신청)
    @PostMapping("/{userId}/mypage/order/return")
    public ResponseEntity<CMResDto<OrderResDto.OrderDetailListResDto>> postOrderReturn(@PathVariable(name = "userId") Long userId,
                                             @Valid @RequestBody OrderReturnReqDto orderReturnReqDto,
                                             @RequestParam(required = false, value = "page", defaultValue = "0") Integer pageNo, HttpServletRequest request) {

        OrderResDto.OrderDetailListResDto orderDetailListResDto = userService.postOrderReturn(orderReturnReqDto.getOrderDetailId(), orderReturnReqDto.getReturnContents(), pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(orderDetailListResDto), HttpStatus.OK);
    }

    // 인증 및 권한 검사 메서드
    private ResponseEntity<CMResDto<String>> checkAuthorization(Long userId, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        String token = authorization.split(" ")[1];
        Long tokenUserId = jwtUtil.getUserId(token);
        if (!Objects.equals(tokenUserId, userId)) {
            return new ResponseEntity<>(CMResDto.errorRes(ErrorCode.FORBIDDEN), HttpStatus.FORBIDDEN);
        }
        return null; // 인증 및 권한 검사가 성공한 경우
    }

}