package com.dmarket.service;

import com.dmarket.domain.product.Category;
import com.dmarket.domain.product.Product;
import com.dmarket.domain.product.ProductReview;
import com.dmarket.domain.product.Qna;
import com.dmarket.domain.user.User;
import com.dmarket.dto.common.ProductDto;
import com.dmarket.dto.common.ProductListDto;
import com.dmarket.dto.common.ProductOptionDto;
import com.dmarket.dto.common.ProductReviewDto;
import com.dmarket.dto.request.ReviewReqDto;
import com.dmarket.dto.response.*;
import com.dmarket.exception.*;
import com.dmarket.repository.product.*;
import com.dmarket.repository.user.UserRepository;
import com.dmarket.repository.user.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.dmarket.exception.ErrorCode.*;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    // 조회가 아닌 메서드들은 꼭 @Transactional 넣어주세요 (CUD, 입력/수정/삭제)
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final QnaRepository qnaRepository;
    private final WishlistRepository wishlistRepository;
    private final ProductImgsRepository productImgsRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductReviewRepository productReviewRepository;

    private static final int PRODUCT_PAGE_POST_COUNT = 16;
    private static final int QNA_PAGE_POST_COUNT = 10;
    private static final int REVIEW_PAGE_POST_COUNT = 5;
    private static final Integer MAX_VALUE = 9999999;

    // 카테고리 전체 목록 depth별로 조회
    public List<CategoryListResDto> getCategories(Integer categoryDepthLevel) {
        return categoryRepository.findByCategoryDepth(categoryDepthLevel);
    }

    // 카테고리별 상품 목록 필터링 조회
    public ProductListResDto getCategoryProducts(int pageNo, Long cateId,
                                                 String sorter, Integer minPrice, Integer maxPrice, Float star) {
        findCategoryById(cateId);
        sorter = sorterValidation(sorter);
        pageNo = pageVaildation(pageNo);
        minPrice = minPrice > MAX_VALUE ? MAX_VALUE : minPrice;
        maxPrice = maxPrice < 0 ? MAX_VALUE : maxPrice;
        star = starValidation(star);

        Pageable pageable = PageRequest.of(pageNo, PRODUCT_PAGE_POST_COUNT, Sort.by(Sort.Direction.DESC, sorter));
        Page<ProductListDto> productList = productRepository.findByCateId(pageable, cateId, minPrice, maxPrice, star);

        return new ProductListResDto(productList.getTotalPages(), productList.getContent());
    }

    // 상품 목록 조건 검색
    public ProductListResDto getSearchProducts(int pageNo, String query,
                                                     String sorter, Integer minPrice, Integer maxPrice, Float star) {
        if (query.isEmpty()) {
            throw new BadRequestException(INVALID_SEARCH_VALUE);
        }
        sorter = sorterValidation(sorter);
        pageNo = pageVaildation(pageNo);
        minPrice = minPrice > MAX_VALUE ? MAX_VALUE : minPrice;
        maxPrice = maxPrice < 0 ? MAX_VALUE : maxPrice;
        star = starValidation(star);

        Pageable pageable = PageRequest.of(pageNo, PRODUCT_PAGE_POST_COUNT, Sort.by(Sort.Direction.DESC, sorter));
        Page<ProductListDto> productList = productRepository.findByQuery(pageable, query, minPrice, maxPrice, star);
        return new ProductListResDto(productList.getTotalPages(), productList.getContent());
    }

    // 최신 상품 조회
    public List<NewProductResDto> findNewProducts() {
        List<NewProductResDto> latestProducts = productRepository.findNewProducts();
        return latestProducts.stream()
                .limit(8)
                .collect(Collectors.toList());
    }

    // 상품 별 Q&A 리스트 조회
    public Page<QnaProductIdListResDto> findQnasByProductId(Long productId, int pageNo) {
        findProductById(productId);
        pageNo = pageVaildation(pageNo);
        Pageable pageable = PageRequest.of(pageNo, QNA_PAGE_POST_COUNT, Sort.by(Sort.Direction.DESC, "qnaId"));
        return qnaRepository.findQnasByProductId(productId, pageable);
    }

    // Q&A 작성
    @Transactional
    public QnaWriteResponseDto qnaWrite(Long productId, Long userId, String qnaTitle, String qnaContents,
                                        Boolean qnaIsSecret) {
        // userId로 회원 이름 가져오기
        User userdata = findUserById(userId);
        String qnaWriter = userdata.getUserName();

        // qna 정보 저장
        Qna qna = Qna.builder()
                .userId(userId)
                .productId(productId)
                .qnaTitle(qnaTitle)
                .qnaContents(qnaContents)
                .qnaSecret(qnaIsSecret)
                .qnaState(false)
                .build();

        Qna savedQna = qnaRepository.save(qna);

        // 반환값 생성
        return new QnaWriteResponseDto(savedQna.getQnaSecret(), qnaWriter, savedQna.getQnaTitle(),
                savedQna.getQnaCreatedDate(), savedQna.getQnaState());
    }

    // 상품 상세 정보 조회
    public ProductInfoResDto getProductInfo(Long productId, Long userId) {
        // 싱품 정보 조회
        Product product = findProductById(productId);
        // 상품의 카테고리 depth 1, depth2 조회 후 합치기
        Category category = findCategoryById(product.getCategoryId());
        String productCategory = category.getParent().getCategoryName() + " / " + category.getCategoryName();
        // 상품의 리뷰 개수 조회
        Long reviewCnt = productReviewRepository.countByProductId(productId);
        // 사용자가 위시리스트에 등록한 상품인지 확인
        Boolean isWish = wishlistRepository.existsByUserIdAndProductId(userId, productId);
        // 상품 옵션 목록, 옵션별 재고 조회
        List<ProductOptionDto> opts = productOptionRepository.findOptionsByProductId(productId);
        // 상품 이미지 목록 조회
        List<String> imgs = productImgsRepository.findAllByProductId(productId);
        // DTO 생성 및 반환
        return new ProductInfoResDto(product, productCategory, reviewCnt, isWish, opts, imgs);
    }

    // 상품별 사용자 리뷰 조회
    public ProductReviewListResDto getReviewList(Long productId, Integer pageNo) {

        pageNo = pageVaildation(pageNo);
        Pageable pageable = PageRequest.of(pageNo, REVIEW_PAGE_POST_COUNT,
                Sort.by(Sort.Direction.DESC, "reviewCreatedDate"));
        // 상품 번호, 상품 별점, 리뷰 개수 조회, 존재하지 않는 상품 번호의 경우 예외 발생
        ProductDto product = findProductByProductId(productId);
        // 상품의 리뷰 목록 불러오기
        Page<ProductReviewDto> reviewList = productReviewRepository.findReviewByProductId(pageable, productId);
        return new ProductReviewListResDto(product, reviewList.getTotalPages(), reviewList.getContent());
    }


    // 추천 상품 조회
    public List<RecommendProductResDto> recommendProduct(Long productId) {
        findProductById(productId);
        // PageRequest의 pageSize 4로 지정 최신 4개만 조회
        return productRepository.findProduct(productId,
                PageRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "productCreatedDate")));
    }

    //리뷰 삭제
    @Transactional
    public void deleteReviewByReviewId(Long productId, Long reviewId) {
        findProductById(productId);
        findReviewById(reviewId);
        productReviewRepository.deleteByReviewId(reviewId);
    }

    // 리뷰 작성
    @Transactional
    public void saveReview(ReviewReqDto reviewReqDto, Long productId) {
        findProductById(productId);
        ProductReview productReview = ProductReview.builder()
                .optionId(reviewReqDto.getOptionId())
                .productId(productId)
                .userId(reviewReqDto.getUserId())
                .reviewRating(reviewReqDto.getReviewRating())
                .reviewContents(reviewReqDto.getReviewContents())
                .reviewImg(reviewReqDto.getReviewImg())
                .build();
        productReviewRepository.save(productReview);
    }

    public ProductDto findProductByProductId(Long productId) {
        return  productRepository.findProductByProductId(productId)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public Category findCategoryById(Long cateId) {
        return categoryRepository.findById(cateId)
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND));
    }

    public User findUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    public ProductReview findReviewById(Long reviewId){
        return productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException(REVIEW_NOT_FOUND));
    }

    // 페이지 예외처리
    public int pageVaildation(int page){
        return page = page > 0 ? page-1 : 0;
    }

    // 정렬 예외처리
    public String sorterValidation(String sorter){
        if(!sorter.equals("productId") && !sorter.equals("reviewCnt") && !sorter.equals("productRating")){
            sorter = "reviewCnt";
        }
        return sorter;
    }

    // 상품 평점 예외처리
    public Float starValidation(Float star){
        if (star < 0 || star > 5) {
            throw new BadRequestException(INVALID_RATING_PARAM);
        }
        return star;
    }

}
