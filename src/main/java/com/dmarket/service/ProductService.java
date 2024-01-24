package com.dmarket.service;

import com.dmarket.domain.product.Qna;
import com.dmarket.domain.user.User;
import com.dmarket.dto.response.*;
import com.dmarket.repository.product.CategoryRepository;
import com.dmarket.repository.product.ProductRepository;
import com.dmarket.repository.product.QnaRepository;
import com.dmarket.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    //조회가 아닌 메서드들은 꼭 @Transactional 넣어주세요 (CUD, 입력/수정/삭제)
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final QnaRepository qnaRepository;


    private static final int PRODUCT_PAGE_POST_COUNT = 16;

    // 카테고리 전체 목록 depth별로 조회
    public List<CategoryListResDto> getCategories(Integer categoryDepthLevel){
        return categoryRepository.findByCategoryDepth(categoryDepthLevel);
    }

    // 카테고리별 상품 목록 필터링 조회
    public Page<ProductListResDto> getCategoryProducts(Pageable pageable, int pageNo, Long cateId,
                                                       String sorter, Integer minPrice, Integer maxPrice, Float star) {
        pageable = PageRequest.of(pageNo, PRODUCT_PAGE_POST_COUNT, Sort.by(Sort.Direction.DESC, sorter));
        Page<ProductListResDto> productList = productRepository.findByCateId(pageable, cateId, minPrice, maxPrice, star);

        return productList;
    }

    // 상품 목록 조건 검색
    public Page<ProductListResDto> getSearchProducts(Pageable pageable, int pageNo, String query,
                                                     String sorter, Integer minPrice, Integer maxPrice, Float star) {
        pageable = PageRequest.of(pageNo, PRODUCT_PAGE_POST_COUNT, Sort.by(Sort.Direction.DESC, sorter));
        Page<ProductListResDto> productList = productRepository.findByQuery(pageable, query, minPrice, maxPrice, star);

        return productList;
    }

    // 최신 상품 조회
    public List<NewProductDto> findNewProducts() {
        return productRepository.findNewProducts();
    }


    // 상품 별 Q&A 리스트 조회
    public Page<QnaProductIdListResDto> findQnasByProductId(Long productId, Pageable pageable) {
        System.out.println("2");
        return qnaRepository.findQnasByProductId(productId, pageable);
    }


    //Q&A 작성
    @Transactional
    public QnaWriteResponseDto qnaWrite(Long productId, Long userId, String qnaTitle, String qnaContents, Boolean qnaIsSecret){
        // userId로 회원 이름 가져오기
        User userdata = userRepository.findUserNameByUserId(userId);
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
        return new QnaWriteResponseDto(savedQna.getQnaSecret(), qnaWriter, savedQna.getQnaTitle(), savedQna.getQnaCreatedDate(), savedQna.getQnaState());
    }



}

