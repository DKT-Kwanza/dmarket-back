package com.dmarket.controller;

import com.dmarket.dto.common.InquiryRequestDto;
import com.dmarket.dto.request.ChangePwdReqDto;
import com.dmarket.dto.request.UserAddressReqDto;
import com.dmarket.dto.response.*;
import com.dmarket.dto.response.CMResDto;
import com.dmarket.dto.response.UserInfoResDto;
import com.dmarket.dto.response.WishlistResDto;
import com.dmarket.dto.request.AddCartReqDto;
import com.dmarket.dto.request.AddWishReqDto;
import com.dmarket.constant.InquiryType;
import com.dmarket.domain.board.Inquiry;
import com.dmarket.dto.common.InquiryRequestDto;
import com.dmarket.dto.response.*;
import com.dmarket.dto.request.*;

import com.dmarket.dto.common.CartListDto;

import com.dmarket.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.AuthenticationException;

import com.dmarket.dto.request.EmailReqDto;
import com.dmarket.dto.request.JoinReqDto;
import com.dmarket.dto.common.CartListDto;
import com.dmarket.domain.board.Inquiry;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody JoinReqDto dto, BindingResult bindingResult) {

        try {
            //유효성 확인
            bindingResultErrorsCheck(bindingResult);
            userService.verifyJoin(dto);

            Long userId = userService.join(dto);
            return new ResponseEntity<>(CMResDto.builder()
                    .code(201).msg("회원가입 성공").data("userId=" + userId).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage(), e.getCause());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("회원가입 실패").data(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            log.warn(e.getMessage(), e.getCause());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg("회원가입 실패").data(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/email")
    public ResponseEntity<?> email(@RequestBody String userEmail) {
        try {
            userService.sendCodeToEmail(userEmail);
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("이메일 인증 코드 전송 완료").build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage(), e.getCause());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("이메일 인증 코드 전송 실패").data(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            log.warn(e.getMessage(), e.getCause());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg("이메일 인증 코드 전송 실패").data(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/email/verify")
    public ResponseEntity<?> emailVerify(@RequestBody EmailReqDto dto) {
        try {
            userService.isValidEmailCode(dto.getUserEmail(), dto.getCode());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("이메일 인증 코드 전송 완료").build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage(), e.getCause());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("이메일 인증 코드 전송 실패").data(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            log.warn(e.getMessage(), e.getCause());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg("이메일 인증 코드 전송 실패").data(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    // 장바구니 추가 api
    @PostMapping("/{userId}/cart")
    public ResponseEntity<?> addCart(@PathVariable Long userId, @Valid @RequestBody AddCartReqDto addCartReqDto,
            BindingResult bindingResult) {
        try {
            // request body 유효성 확인
            bindingResultErrorsCheck(bindingResult);

            // 장바구니 추가
            Long productId = addCartReqDto.getProductId();
            Long optionId = addCartReqDto.getOptionId();
            Integer productCount = addCartReqDto.getProductCount();
            userService.addCart(userId, productId, optionId, productCount);

            return new ResponseEntity<>(CMResDto.builder().code(200).msg("장바구니 등록 성공").build(), HttpStatus.OK);
        } catch (RuntimeException e) {
            log.warn(e.getMessage(), e.getCause());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").data(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("서버 내부 오류").build(), HttpStatus.BAD_REQUEST);
        }
    }

    // 위시리스트 추가 api
    @PostMapping("/{userId}/wish")
    public ResponseEntity<?> addWish(@PathVariable Long userId, @Valid @RequestBody AddWishReqDto addWishReqDto,
            BindingResult bindingResult) {
        try {
            // request body 유효성 확인
            bindingResultErrorsCheck(bindingResult);

            // 위시리스트 추가
            Long productId = addWishReqDto.getProductId();
            userService.addWish(userId, productId);

            return new ResponseEntity<>(CMResDto.builder().code(200).msg("위시리스트 등록 성공").build(), HttpStatus.OK);
        } catch (RuntimeException e) {
            log.warn(e.getMessage(), e.getCause());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").data(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(CMResDto.builder().code(400).msg("서버 내부 오류").build(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user")
    public String userP() {
        return "User Page";
    }

    // 위시리스트 조회
    @GetMapping("/{userId}/wish")
    public ResponseEntity<?> getWishlistByUserId(@PathVariable(name = "userId") Long userId) {
        try {
            WishlistResDto wishlist = userService.getWishlistByUserId(userId);
            log.info("데이터 조회 완료");
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("위시리스트 조회 완료").data(wishlist).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // 잘못된 요청에 대한 예외 처리
            log.warn("유효하지 않은 요청 메시지: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").build(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // 기타 예외에 대한 예외 처리
            log.error("서버 내부 오류: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg("서버 내부 오류").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 장바구니 상품 개수 조회
    @GetMapping("{userId}/cart-count")
    public ResponseEntity<?> getCartCount(@PathVariable(name = "userId") Long userId) {
        try {
            CartCountResDto cartCount = userService.getCartCount(userId);
            log.info("데이터 조회 완료");
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("장바구니 상품 개수 조회 완료").data(cartCount).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // 잘못된 요청에 대한 예외 처리
            log.warn("유효하지 않은 요청 메시지:" + e.getMessage());
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

    // 마이페이지 서브헤더 사용자 정보 및 마일리지 조회
    @GetMapping("/{userId}/mypage/mileage")
    public ResponseEntity<?> getSubHeader(@PathVariable(name = "userId") Long userId) {
        try {
            UserHeaderInfoResDto subHeader = userService.getSubHeader(userId);
            log.info("데이터 조회 완료");
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("마이페이지 서브 헤더 조회 완료").data(subHeader).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // 잘못된 요청에 대한 예외 처리
            log.warn("유효하지 않은 요청 메시지:" + e.getMessage());
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

    // 위시리스트 삭제
    @DeleteMapping("/{userId}/wish/{wishlistIds}")
    public ResponseEntity<?> deleteWishlistId(@PathVariable(name = "userId") Long userId,
            @PathVariable(name = "wishlistIds") List<Long> wishlistIds) {
        try {
            for (Long wishlistId : wishlistIds) {
                userService.deleteWishlistById(wishlistId);
            }
            log.info("데이터 삭제 완료");
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("위시리스트 삭제 완료").build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // 잘못된 요청에 대한 예외 처리
            log.warn("유효하지 않은 요청 메시지: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").build(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // 기타 예외에 대한 예외 처리
            log.error("서버 내부 오류: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg("서버 내부 오류").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 사용자 정보 조회
    @GetMapping("/{userId}/mypage/myinfo")
    public ResponseEntity<?> getUserInfoByUserId(@PathVariable(name = "userId") Long userId) {
        try {
            UserInfoResDto userInfo = userService.getUserInfoByUserId(userId);
            log.info("데이터 조회 완료");
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("사용자 정보 조회 완료").data(userInfo).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // 잘못된 요청에 대한 예외 처리
            log.warn("유효하지 않은 요청 메시지: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").build(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // 기타 예외에 대한 예외 처리
            log.error("서버 내부 오류: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg("서버 내부 오류").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 사용자 비밀번호 변경
    @PutMapping("{userId}/mypage/change-pwd")
    public ResponseEntity<?> updatePassword(HttpServletRequest request,
                                            @PathVariable(name = "userId") Long userId,
                                            @Valid @RequestBody ChangePwdReqDto changePwdReqDto,
                                            BindingResult bindingResult){
        try {
            bindingResultErrorsCheck(bindingResult);
            String currentPassword = changePwdReqDto.getCurrentPassword();
            String newPassword = changePwdReqDto.getNewPassword();

            userService.validatePassword(request, currentPassword);
            userService.updatePassword(newPassword, userId);

            log.info("데이터 변경 완료");
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("비밀번호 변경 완료").build(), HttpStatus.OK);
        }
        catch (IllegalArgumentException e) {
            // 잘못된 요청에 대한 예외 처리
            log.warn("유효하지 않은 요청 메시지: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            // 기타 예외에 대한 예외 처리
            log.error("서버 내부 오류: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 사용자 배송지 수정
    @PutMapping("{userId}/mypage/myinfo")
    public ResponseEntity<?> updateAddress(HttpServletRequest request,
                                           @PathVariable(name = "userId") Long userId,
                                           @Valid @RequestBody UserAddressReqDto userAddressReqDto,
                                           BindingResult bindingResult){
        try {
            bindingResultErrorsCheck(bindingResult);

            UserAddressResDto result = userService.updateAddress(request, userId, userAddressReqDto);
            log.info("데이터 변경 완료");
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("배송지 변경 완료").build(), HttpStatus.OK);
        }
        catch (IllegalArgumentException e) {
            // 잘못된 요청에 대한 예외 처리
            log.warn("유효하지 않은 요청 메시지: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            // 기타 예외에 대한 예외 처리
            log.error("서버 내부 오류: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 장바구니 조회
    @GetMapping("/{userId}/cart")
    public ResponseEntity<?> getCarts(@PathVariable Long userId) {
        try {
            List<CartListDto> cartListDtos = userService.getCartsfindByUserId(userId);
            System.out.println(cartListDtos);
            TotalCartResDto totalCartResDto = new TotalCartResDto(cartListDtos);
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("장바구니 조회").data(totalCartResDto).build(), HttpStatus.OK);
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

    // 장바구니 삭제
    @DeleteMapping("/{userId}/cart/{cartIds}")
    public ResponseEntity<?> deleteCart(@PathVariable Long userId, @PathVariable(name = "cartIds") List<Long> cartIds) {
        try {
            for (Long cartId : cartIds) {
                userService.deleteCartByCartId(userId, cartId);
                log.info("데이터 삭제 완료");
            }

            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("장바구니에서 삭제 완료").build(), HttpStatus.OK);

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

    // 작성한 Qna 조회
    @GetMapping("/{userId}/mypage/qna")
    public ResponseEntity<?> getQna(@PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        try {
            page = page > 0 ? page - 1 : page;
            if (page < 0 || size <= 0) {
                return new ResponseEntity<>(CMResDto.builder().code(400).msg("유효하지 않은 페이지 또는 크기").build(),
                        HttpStatus.BAD_REQUEST);
            }
            Pageable pageable = PageRequest.of(page, size);
            Page<QnaResDto> qnaListResDtos = userService.getQnasfindByUserId(userId, pageable);
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("작성한 상품 QnA 목록 조회 완료").data(qnaListResDtos).build(), HttpStatus.OK);

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

    // 리뷰 작성 가능한 상품 목록 조회
    @GetMapping("/{userId}/mypage/available-reviews")
    public ResponseEntity<?> getAvailableReviews(@PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size) {
        try {
            page = page > 0 ? page - 1 : page;
            if (page < 0 || size <= 0) {
                return new ResponseEntity<>(CMResDto.builder().code(400).msg("유효하지 않은 페이지 또는 크기").build(),
                        HttpStatus.BAD_REQUEST);
            }
            Pageable pageable = PageRequest.of(page, size);
            Page<OrderResDto> orderResDtos = userService.getOrderDetailsWithoutReviewByUserId(userId, pageable);
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("리뷰 작성 가능한 상품 목록").data(orderResDtos).build(), HttpStatus.OK);

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

    // 작성한 리뷰 목록 조회
    @GetMapping("/{userId}/mypage/written-reviews")
    public ResponseEntity<?> getWrittenReviews(@PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size) {
        try {
            page = page > 0 ? page - 1 : page;
            if (page < 0 || size <= 0) {
                return new ResponseEntity<>(CMResDto.builder().code(400).msg("유효하지 않은 페이지 또는 크기").build(),
                        HttpStatus.BAD_REQUEST);
            }
            Pageable pageable = PageRequest.of(page, size);
            Page<OrderResDto> orderResDtos = userService.getOrderDetailsWithReviewByUserId(userId, pageable);
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("작성한 리뷰 조회 완료").data(orderResDtos).build(), HttpStatus.OK);

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

    // 문의 작성
    @PostMapping("/{userId}/board/inquiry")
    public ResponseEntity<Inquiry> createInquiry(
            @PathVariable Long userId,
            @RequestBody InquiryRequestDto inquiryRequestDto) {
        try {
            Inquiry inquiry = Inquiry.builder()
                    .userId(userId)
                    .inquiryType(inquiryRequestDto.getInquiryType())
                    .inquiryTitle(inquiryRequestDto.getInquiryTitle())
                    .inquiryContents(inquiryRequestDto.getInquiryContents())
                    .inquiryImg(inquiryRequestDto.getInquiryImg())
                    .inquiryState(false) // 기본값 false로 설정
                    .build();

            Inquiry createdInquiry = userService.createInquiry(inquiry);
            return new ResponseEntity<>(createdInquiry, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //validation 체크
    private void bindingResultErrorsCheck(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError fe : bindingResult.getFieldErrors()) {
                errorMap.put(fe.getField(), fe.getDefaultMessage());
            }
            throw new IllegalArgumentException(errorMap.toString());
        }
    }
}
