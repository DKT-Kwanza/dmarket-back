package com.dmarket.controller;

import com.dmarket.dto.response.CMResDto;
import com.dmarket.dto.response.CategoryListResDto;
import com.dmarket.dto.response.ProductListResDto;
import com.dmarket.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 카테고리별 상품 목록 조건 조회 api
    @GetMapping(value = "/categories/{cateId}")
    public ResponseEntity<?> getCategoryProducts(@PathVariable Long cateId,
                                                 @RequestParam(required = false, value = "sorter", defaultValue = "reviewCnt") String sorter,
                                                 @RequestParam(required = false, value = "min-price", defaultValue = "0") Integer minPrice,
                                                 @RequestParam(required = false, value = "max-price", defaultValue = "9999999") Integer maxPrice,
                                                 @RequestParam(required = false, value = "star", defaultValue = "0.0F") Float star,
                                                 @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo,
                                                 Pageable pageable){
        try{
            pageNo = pageNo > 0 ? pageNo-1 : pageNo;
            Page<ProductListResDto> products = productService.getCategoryProducts(pageable, pageNo , cateId,
                    sorter, minPrice, maxPrice, star);
            log.info("데이터 조회 완료");
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("카테고리별 상품 목록 조회 완료").data(products).build(), HttpStatus.OK);
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

    // 상품 목록 조건 검색 api
    @GetMapping("/search")
    public ResponseEntity<?> getSearchProducts(@RequestParam(required = true, value = "q") String query,
                                               @RequestParam(required = false, value = "sorter", defaultValue = "reviewCnt") String sorter,
                                               @RequestParam(required = false, value = "min-price", defaultValue = "0") Integer minPrice,
                                               @RequestParam(required = false, value = "max-price", defaultValue = "9999999") Integer maxPrice,
                                               @RequestParam(required = false, value = "star", defaultValue = "0.0F") Float star,
                                               @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo,
                                               Pageable pageable){
        try{
            pageNo = pageNo > 0 ? pageNo-1 : pageNo;
            Page<ProductListResDto> products = productService.getSearchProducts(pageable, pageNo , query,
                    sorter, minPrice, maxPrice, star);
            log.info("데이터 조회 완료");
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("카테고리별 상품 목록 조회 완료").data(products).build(), HttpStatus.OK);
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
