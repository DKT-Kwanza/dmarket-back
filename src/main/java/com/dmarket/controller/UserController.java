package com.dmarket.controller;

import com.dmarket.domain.board.Inquiry;
import com.dmarket.domain.user.User;
import com.dmarket.dto.common.CartListDto;
import com.dmarket.dto.common.InquiryRequestDto;
import com.dmarket.dto.request.*;
import com.dmarket.dto.response.*;
import com.dmarket.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody JoinReqDto dto) {

        userService.verifyJoin(dto);
        Long userId = userService.join(dto);
        return new ResponseEntity<>(CMResDto.successDataRes("userId=" + userId), HttpStatus.OK);
    }

    // 이메일 인증 코드 전송
    @PostMapping("/email")
    public ResponseEntity<?> email(@RequestBody String userEmail) {
        userService.sendCodeToEmail(userEmail);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    //이메일 인증 코드 확인
    @PostMapping("/email/verify")
    public ResponseEntity<?> emailVerify(@RequestBody EmailReqDto dto) {
        userService.isValidEmailCode(dto.getUserEmail(), dto.getCode());
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 장바구니 추가 api
    @PostMapping("/{userId}/cart")
    public ResponseEntity<?> addCart(@PathVariable Long userId, @Valid @RequestBody AddCartReqDto addCartReqDto) {

        Long productId = addCartReqDto.getProductId();
        Long optionId = addCartReqDto.getOptionId();
        Integer productCount = addCartReqDto.getProductCount();
        userService.addCart(userId, productId, optionId, productCount);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 위시리스트 추가 api
    @PostMapping("/{userId}/wish")
    public ResponseEntity<?> addWish(@PathVariable Long userId, @Valid @RequestBody AddWishReqDto addWishReqDto) {
        // 위시리스트 추가
        Long productId = addWishReqDto.getProductId();
        userService.addWish(userId, productId);

        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    @GetMapping("/user")
    public String userP() {
        return "User Page";
    }

    // 위시리스트 조회
    @GetMapping("/{userId}/wish")
    public ResponseEntity<?> getWishlistByUserId(@PathVariable(name = "userId") Long userId) {

        WishlistResDto wishlist = userService.getWishlistByUserId(userId);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(wishlist), HttpStatus.OK);
    }

    // 장바구니 상품 개수 조회
    @GetMapping("{userId}/cart-count")
    public ResponseEntity<?> getCartCount(@PathVariable(name = "userId") Long userId) {

        CartCountResDto cartCount = userService.getCartCount(userId);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(cartCount), HttpStatus.OK);
    }

    // 마이페이지 서브헤더 사용자 정보 및 마일리지 조회
    @GetMapping("/{userId}/mypage/mileage")
    public ResponseEntity<?> getSubHeader(@PathVariable(name = "userId") Long userId) {

        UserHeaderInfoResDto subHeader = userService.getSubHeader(userId);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(subHeader), HttpStatus.OK);
    }

    // 위시리스트 삭제
    @DeleteMapping("/{userId}/wish/{wishlistIds}")
    public ResponseEntity<?> deleteWishlistId(@PathVariable(name = "userId") Long userId,
                                              @PathVariable(name = "wishlistIds") List<Long> wishlistIds) {
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
        request.getHeader("Authorization");
        UserInfoResDto userInfo = userService.getUserInfoByUserId(userId);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(userInfo),HttpStatus.OK);
    }

    // 사용자 비밀번호 변경
    @PutMapping("{userId}/mypage/change-pwd")
    public ResponseEntity<?> updatePassword(HttpServletRequest request,
                                            @PathVariable(name = "userId") Long userId,
                                            @Valid @RequestBody ChangePwdReqDto changePwdReqDto) {
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
                                            @Valid @RequestBody UserAddressReqDto userAddressReqDto) {
        UserAddressResDto result = userService.updateAddress(request, userId, userAddressReqDto);
        log.info("데이터 변경 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(result), HttpStatus.OK);
    }

    // 장바구니 조회
    @GetMapping("/{userId}/cart")
    public ResponseEntity<?> getCarts(@PathVariable Long userId) {

        List<CartListDto> cartListDtos = userService.getCartsfindByUserId(userId);
        System.out.println(cartListDtos);
        TotalCartResDto totalCartResDto = new TotalCartResDto(cartListDtos);
        return new ResponseEntity<>(CMResDto.successDataRes(totalCartResDto), HttpStatus.OK);
    }

    // 장바구니 삭제
    @DeleteMapping("/{userId}/cart/{cartIds}")
    public ResponseEntity<?> deleteCart(@PathVariable Long userId, @PathVariable(name = "cartIds") List<Long> cartIds) {

        for (Long cartId : cartIds) {
            userService.deleteCartByCartId(userId, cartId);
            log.info("데이터 삭제 완료");
        }
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 작성한 Qna 조회
    @GetMapping("/{userId}/mypage/qna")
    public ResponseEntity<?> getQna(@PathVariable Long userId,
                                    @RequestParam(required = false, value = "page", defaultValue = "0") Integer pageNo) {

        Page<QnaResDto> qnaListResDtos = userService.getQnasfindByUserId(userId, pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(qnaListResDtos), HttpStatus.OK);
    }

    // 리뷰 작성 가능한 상품 목록 조회
    @GetMapping("/{userId}/mypage/available-reviews")
    public ResponseEntity<?> getAvailableReviews(@PathVariable Long userId,
                                                @RequestParam(required = false, value = "page", defaultValue = "0") Integer pageNo) {

        Page<OrderResDto> orderResDtos = userService.getOrderDetailsWithoutReviewByUserId(userId, pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(orderResDtos), HttpStatus.OK);
    }

    // 작성한 리뷰 목록 조회
    @GetMapping("/{userId}/mypage/written-reviews")
    public ResponseEntity<?> getWrittenReviews(@PathVariable Long userId,
                                                @RequestParam(required = false, value = "page", defaultValue = "0") Integer pageNo) {

        Page<OrderResDto> orderResDtos = userService.getOrderDetailsWithReviewByUserId(userId, pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(orderResDtos), HttpStatus.OK);
    }

    // 문의 작성
    @PostMapping("/{userId}/board/inquiry")
    public ResponseEntity<CMResDto> createInquiry(@PathVariable Long userId,
                                                  @RequestBody InquiryRequestDto inquiryRequestDto) {

        Inquiry inquiry = Inquiry.builder()
                .userId(userId)
                .inquiryType(inquiryRequestDto.getInquiryType())
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
                                             @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {
        // 충전 요청
        MileageListResDto res = userService.getMileageUsage(userId, pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(res), HttpStatus.OK);
    }

    // 마일리지 충전 요청 api
    @PostMapping("/{userId}/mypage/mileage-charge")
    public ResponseEntity<?> mileageChargeReq(@PathVariable Long userId,
                                              @Valid @RequestBody MileageChargeReqDto mileageChargeReqDto) {
        // 충전 요청
        userService.mileageChargeReq(userId, mileageChargeReqDto.getMileageCharge());
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 작성한 고객 문의 목록
    @GetMapping("/{userId}/mypage/inquiry")
    public ResponseEntity<?> getUserInquiryAllByUserId(@PathVariable(name = "userId") Long userId) {

        List<UserInquiryAllResDto> userInquiryAllResDtos = userService.getUserInquiryAllbyUserId(userId);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(userInquiryAllResDtos), HttpStatus.OK);
    }
    // 사용자 주문 내역 상세 조회
    @GetMapping("/{userId}/mypage/orders/{orderId}")
    public ResponseEntity<?> getUserOrderDetailListByOrderId(@PathVariable(name = "userId") Long userId,
                                                             @PathVariable(name = "orderId") Long orderId) {
        OrderDetailListResDto userOrderDetailResDtos = userService.getOrderDetailListByOrderId(userId, orderId);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(userOrderDetailResDtos), HttpStatus.OK);
    }
    // 주문 / 배송 내역 조회
    @GetMapping("/{userId}/mypage/orders")
    public ResponseEntity<?> getUserOrderList(@PathVariable(name="userId") Long userId) {

        OrderListResDto userOrderListResDtos = userService.getOrderListResByUserId(userId);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(userOrderListResDtos), HttpStatus.OK);
    }

}
