package com.dmarket.controller;

import com.dmarket.dto.common.CartListDto;
import com.dmarket.dto.response.*;
import com.dmarket.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.AuthenticationException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

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
    public ResponseEntity<?> getQna(@PathVariable Long userId) {
        try {
            List<QnaResDto> qnaListResDtos = userService.getQnasfindByUserId(userId);
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
    public ResponseEntity<?> getAvailableReviews(@PathVariable Long userId) {
        try {
            List<OrderResDto> orderResDtos = userService.getOrderDetailsWithoutReviewByUserId(userId);
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
        public ResponseEntity<?> getWrittenReviews(@PathVariable Long userId) {
            try {
                List<OrderResDto> orderResDtos = userService.getOrderDetailsWithReviewByUserId(userId);
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

}
