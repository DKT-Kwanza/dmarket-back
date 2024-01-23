package com.dmarket.controller;

import com.dmarket.dto.response.CMResDto;
import com.dmarket.dto.response.CategoryListResDto;
import com.dmarket.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    // 카테고리 전체 목록 depth별로 조회 api
    @GetMapping(value = "/categories")
    public ResponseEntity<?> getCategories() {
        try{
            List<CategoryListResDto> categories = productService.getCategories(1);
            log.info("데이터 조회 완료");
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("카테고리 목록 조회 완료").data(categories).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // 잘못된 요청에 대한 예외 처리
            log.warn("유효하지 않은 요청 메시지:" + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").build(), HttpStatus.BAD_REQUEST);
//        } catch (AuthenticationException e) {
//            // 인증 오류에 대한 예외 처리
//            log.warn("유효하지 않은 인증" + e.getMessage());
//            return new ResponseEntity<>(CMResDto.builder()
//                    .code(401).msg("유효하지 않은 인증").build(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            // 기타 예외에 대한 예외 처리
            log.error("서버 내부 오류: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg("서버 내부 오류").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
