package com.dmarket.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.dmarket.domain.document.ProductDocument;
import com.dmarket.dto.request.QnaReqDto;
import com.dmarket.dto.request.ReviewReqDto;
import com.dmarket.dto.response.CMResDto;
import com.dmarket.dto.response.CategoryResDto;
import com.dmarket.dto.response.ProductResDto;
import com.dmarket.dto.response.QnaResDto;
import com.dmarket.service.ProductService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;


    /**
     * 카테고리: Category
     */
    // 카테고리 전체 목록 depth별로 조회 api
    @GetMapping(value = "/categories")
    public ResponseEntity<CMResDto<List<CategoryResDto.CategoryListResDto>>> getCategories() {
        List<CategoryResDto.CategoryListResDto> categories = productService.getCategories(1);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(categories), HttpStatus.OK);
    }


    /**
     * 상품: Product
     */
    // 카테고리별 상품 목록 조건 조회 api
    @GetMapping(value = "/categories/{cateId}")
    public ResponseEntity<CMResDto<Page<ProductResDto.ProductListResDto>>> getCategoryProducts(@PathVariable Long cateId,
            @RequestParam(required = false, value = "sorter", defaultValue = "reviewCnt") String sorter,
            @RequestParam(required = false, value = "min-price", defaultValue = "0") Integer minPrice,
            @RequestParam(required = false, value = "max-price", defaultValue = "9999999") Integer maxPrice,
            @RequestParam(required = false, value = "star", defaultValue = "0.0F") Float star,
            @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {
        Page<ProductResDto.ProductListResDto> products = productService.getCategoryProducts(pageNo, cateId, sorter, minPrice, maxPrice, star);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(products), HttpStatus.OK);
    }

    // 상품 목록 조건 검색 api
    @GetMapping("/search")
    public ResponseEntity<CMResDto<ProductResDto.ProductSearchListResDto>> getSearchProducts(@RequestParam(required = true, value = "q") String query,
            @RequestParam(required = false, value = "sorter", defaultValue = "reviewCnt") String sorter,
            @RequestParam(required = false, value = "min-price", defaultValue = "0") Integer minPrice,
            @RequestParam(required = false, value = "max-price", defaultValue = "9999999") Integer maxPrice,
            @RequestParam(required = false, value = "star", defaultValue = "0") Float star,
            @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) throws IOException {
        //Page<ProductResDto.ProductListResDto> products = productService.getSearchProducts(pageNo, query, sorter, minPrice, maxPrice, star);
        ProductResDto.ProductSearchListResDto products = productService.getSearchProducts(pageNo , query,
                sorter, minPrice, maxPrice, star);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(products), HttpStatus.OK);
    }

    // 최신 상품 조회
    @GetMapping("/new-products")
    public ResponseEntity<CMResDto<List<Object>>> getLatestProducts() {
        List<ProductResDto.NewProductResDto> latestProducts = productService.findNewProducts();
        // response format mapping
        List<Object> responseData = productService.mapToResponseFormat(latestProducts, 16);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(responseData), HttpStatus.OK);
    }

    // 전체 카테고리 할인율 높은 순으로 상품 limit개 불러오기
    @GetMapping("/high-discount-rate")
    public ResponseEntity<CMResDto<List<Object>>> getHighDiscountRateProducts() {
        int limit = 16;
        List<ProductResDto.NewProductResDto> dtos = productService.findHighDiscountRateProducts(limit);
    
        // response format mapping
        List<Object> responseData = productService.mapToResponseFormat(dtos, limit);
        log.info("전체 카테고리 할인율 높은 순으로 상품 " + limit + "개 조회");
        return new ResponseEntity<>(CMResDto.<List<Object>>successDataRes(responseData), HttpStatus.OK);
    }

    // 카테고리 별 할인율 높은 순으로 상품 limit개 불러오기
    @GetMapping("/high-discount-rate/{cateId}")
    public ResponseEntity<CMResDto<List<Object>>> getHighDiscountRateProducts(@PathVariable Long cateId) {
        int limit = 16;
        List<ProductResDto.NewProductResDto> dtos = productService.findHighDiscountRateProducts(cateId, limit);

        // response format mapping
        List<Object> responseData = productService.mapToResponseFormat(dtos, limit);
        log.info("카테고리별 할인율 높은 순으로 상품 " + limit + "개 조회");
        return new ResponseEntity<>(CMResDto.successDataRes(responseData), HttpStatus.OK);
    }

    // 상품 상세 조회 api
    @GetMapping("/{productId}")
    public ResponseEntity<CMResDto<ProductResDto.ProductInfoResDto>> getProductInfo(@PathVariable Long productId) {
        ProductResDto.ProductInfoResDto res = productService.getProductInfo(productId);
        return new ResponseEntity<>(CMResDto.successDataRes(res), HttpStatus.OK);
    }

    // 추천 상품 조회 api
    @GetMapping("/{productId}/recommend")
    public ResponseEntity<CMResDto<List<ProductResDto.RecommendProductResDto>>> recommendProduct(@PathVariable Long productId) {
        List<ProductResDto.RecommendProductResDto> res = productService.recommendProduct(productId);
        return new ResponseEntity<>(CMResDto.successDataRes(res), HttpStatus.OK);
    }


    /**
     * QnA
     */
    // 상품 별 Q&A 리스트 조회
    @GetMapping("/{productId}/qnaList")
    public ResponseEntity<CMResDto<Page<QnaResDto.QnaProductIdListResDto>>> getAnasByProductId(@PathVariable Long productId,
            @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {
        Page<QnaResDto.QnaProductIdListResDto> qnaList = productService.findQnasByProductId(productId, pageNo);

        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(qnaList), HttpStatus.OK);
    }

    // Q&A 작성 API
    @PostMapping("/{productId}/qna")
    public ResponseEntity<CMResDto<QnaResDto.QnaWriteResponseDto>> saveQnaAboutProduct(@PathVariable Long productId,
            @RequestBody QnaReqDto.QnaWriteReqDto qnaWriteReqDto) {
        QnaResDto.QnaWriteResponseDto qnaWriteRespone = productService.qnaWrite(productId, qnaWriteReqDto.getUserId(),
                qnaWriteReqDto.getQnaTitle(), qnaWriteReqDto.getQnaContents(), qnaWriteReqDto.getQnaIsSecret());
        log.info("QnA 저장 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(qnaWriteRespone), HttpStatus.OK);
    }


    /**
     * 리뷰: Review
     */
    // 상품별 사용자 리뷰 조회
    @GetMapping("/{productId}/reviews")
    public ResponseEntity<CMResDto<ProductResDto.ProductReviewListResDto>> getProductReviews(@PathVariable Long productId,
                                               @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {

        ProductResDto.ProductReviewListResDto res = productService.getReviewList(productId, pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(res), HttpStatus.OK);
    }

    // 마이페이지 리뷰 작성
    @PostMapping("{productId}/review")
    public ResponseEntity<CMResDto<String>> saveReview(@PathVariable Long productId, @Valid @RequestBody ReviewReqDto reviewReqDto) {
        productService.saveReview(reviewReqDto, productId);
        log.info("데이터 저장 완료");
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }
    

    // 리뷰 삭제 api
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<CMResDto<String>> deleteReview(@PathVariable Long reviewId) {
        productService.deleteReviewByReviewId(reviewId);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

}
