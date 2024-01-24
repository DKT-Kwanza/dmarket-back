package com.dmarket.controller;

import com.dmarket.dto.response.CMResDto;
import com.dmarket.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.AuthenticationException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    ProductService productService;

    // 리뷰 삭제
    @DeleteMapping("/{productId}/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long productId, @PathVariable Long reviewId) {
        try {
            productService.deleteReviewByReviewId(productId, reviewId);
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("리뷰 삭제 완료").build(), HttpStatus.OK);

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
