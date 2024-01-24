package com.dmarket.service;

import com.dmarket.domain.product.Category;
import com.dmarket.domain.product.Product;
import com.dmarket.dto.ProductOptionDto;
import com.dmarket.dto.response.ProductInfoResDto;
import com.dmarket.repository.product.*;
import com.dmarket.repository.user.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ProductImgsRepository productImgsRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductReviewRepository productReviewRepository;
    private final WishlistRepository wishlistRepository;

    // 상품 상세 정보 조회
    public ProductInfoResDto getProductInfo(Long productId, Long userId){
        // 싱품 정보 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 상품입니다."));
        // 상품의 카테고리 depth 1, depth2 조회 후 합치기
        Category category = categoryRepository.findByCategoryId(product.getCategoryId());
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
}
