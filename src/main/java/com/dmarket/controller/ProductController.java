package com.dmarket.controller;

import com.dmarket.dto.request.QnaReqDto;
import com.dmarket.dto.request.ReviewReqDto;
import com.dmarket.dto.response.CMResDto;
import com.dmarket.dto.response.CategoryResDto;
import com.dmarket.dto.response.ProductResDto;
import com.dmarket.dto.response.QnaResDto;
import com.dmarket.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    // 카테고리 전체 목록 depth별로 조회 api
    @GetMapping(value = "/categories")
    public ResponseEntity<CMResDto<?>> getCategories() {
        List<CategoryResDto.CategoryListResDto> categories = productService.getCategories(1);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(categories), HttpStatus.OK);
    }

    // 카테고리별 상품 목록 조건 조회 api
    @GetMapping(value = "/categories/{cateId}")
    public ResponseEntity<CMResDto<?>> getCategoryProducts(@PathVariable Long cateId,
                                                         @RequestParam(required = false, value = "sorter", defaultValue = "reviewCnt") String sorter,
                                                         @RequestParam(required = false, value = "min-price", defaultValue = "0") Integer minPrice,
                                                         @RequestParam(required = false, value = "max-price", defaultValue = "9999999") Integer maxPrice,
                                                         @RequestParam(required = false, value = "star", defaultValue = "0.0F") Float star,
                                                         @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo){
        Page<ProductResDto.ProductListResDto> products = productService.getCategoryProducts(pageNo , cateId, sorter, minPrice, maxPrice, star);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(products), HttpStatus.OK);
    }

    // 상품 목록 조건 검색 api
    @GetMapping("/search")
    public ResponseEntity<CMResDto<?>> getSearchProducts(@RequestParam(required = true, value = "q") String query,
                                                       @RequestParam(required = false, value = "sorter", defaultValue = "reviewCnt") String sorter,
                                                       @RequestParam(required = false, value = "min-price", defaultValue = "0") Integer minPrice,
                                                       @RequestParam(required = false, value = "max-price", defaultValue = "9999999") Integer maxPrice,
                                                       @RequestParam(required = false, value = "star", defaultValue = "0") Float star,
                                                       @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo){
        Page<ProductResDto.ProductListResDto> products = productService.getSearchProducts(pageNo , query,
                sorter, minPrice, maxPrice, star);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(products), HttpStatus.OK);
    }

    // 최신 상품 조회
    @GetMapping("/new-products")
    public ResponseEntity<CMResDto<?>> getLatestProducts() {
        List<ProductResDto.NewProductResDto> latestProducts = productService.findNewProducts();
        // response format mapping
        List<Object> responseData = productService.mapToResponseFormat(latestProducts);
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(responseData), HttpStatus.OK);
    }

    // QnA
    // 상품 별 Q&A 리스트 조회
    @GetMapping("/{productId}/qna")
    public ResponseEntity<CMResDto<?>> getQnasByProdcutId(@PathVariable Long productId,
                                                          @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {
        Page<QnaResDto.QnaProductIdListResDto> qnaList = productService.findQnasByProductId(productId, pageNo);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("qnaCount", qnaList.getTotalElements());
        responseData.put("totalPage", qnaList.getTotalPages());
        responseData.put("qnaList", qnaList.getContent());
        //totalpage수도 반환해주도록 수정함. 되도록이면 dto로 반환하도록 수정하는 것이 좋을 것 같음!
        log.info("데이터 조회 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(responseData), HttpStatus.OK);
    }

    //Q&A 작성 API
    @PostMapping("/{productId}/qna")
    public ResponseEntity<?> saveQnaAboutProduct(@PathVariable Long productId, @RequestBody QnaReqDto.QnaWriteReqDto qnaWriteReqDto) {
        QnaResDto.QnaWriteResponseDto qnaWriteRespone = productService.qnaWrite(productId,qnaWriteReqDto.getUserId(),qnaWriteReqDto.getQnaTitle(),qnaWriteReqDto.getQnaContents(),qnaWriteReqDto.getQnaIsSecret());
        log.info("QnA 저장 완료");
        return new ResponseEntity<>(CMResDto.successDataRes(qnaWriteRespone), HttpStatus.OK);
    }

    // 상품 상세 조회 api
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductInfo(@PathVariable Long productId) {
        ProductResDto.ProductInfoResDto res = productService.getProductInfo(productId);
        return new ResponseEntity<>(CMResDto.successDataRes(res), HttpStatus.OK);
    }

    // 상품별 사용자 리뷰 조회 api
    @GetMapping("/{productId}/reviews")
    public ResponseEntity<?> getProductReviews(@PathVariable Long productId,
                                               @RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {

        ProductResDto.ProductReviewListResDto res = productService.getReviewList(productId, pageNo);
        return new ResponseEntity<>(CMResDto.successDataRes(res), HttpStatus.OK);
    }

    // 마이페이지 리뷰 작성
    @PostMapping("{productId}/review")
    public ResponseEntity<?> saveReview(@PathVariable Long productId,
                                        @Valid @RequestBody ReviewReqDto reviewReqDto) {

        productService.saveReview(reviewReqDto, productId);
        log.info("데이터 저장 완료");
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

    // 추천 상품 조회 api
    @GetMapping("/{productId}/recommend")
    public ResponseEntity<?> recommendProduct(@PathVariable Long productId) {
        List<ProductResDto.RecommendProductResDto> res = productService.recommendProduct(productId);
        return new ResponseEntity<>(CMResDto.successDataRes(res), HttpStatus.OK);
    }

    // 리뷰 삭제 api
    @DeleteMapping("/{productId}/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long productId,
                                          @PathVariable Long reviewId) {
        productService.deleteReviewByReviewId(productId, reviewId);
        return new ResponseEntity<>(CMResDto.successNoRes(), HttpStatus.OK);
    }

}
