package com.dmarket.service;

import com.dmarket.dto.response.CategoryListResDto;
import com.dmarket.dto.response.NewProductResDto;
import com.dmarket.dto.response.ProductListResDto;
import com.dmarket.repository.product.CategoryRepository;
import com.dmarket.repository.product.ProductRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    //조회가 아닌 메서드들은 꼭 @Transactional 넣어주세요 (CUD, 입력/수정/삭제)
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

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
    public List<NewProductResDto> findNewProducts() {
        return productRepository.findNewProducts();
    }
    // 최신 상품 조회 - 매핑
    public List<Object> mapToResponseFormat(List<NewProductResDto> latestProducts) {
        return latestProducts.stream()
                .limit(8).map(product -> new Object() {
                    public final Long productId = product.getProductId();
                    public final String productBrand = product.getProductBrand();
                    public final String productName = product.getProductName();
                    public final String productImg = product.getProductImg();
                    public final String productSalePrice = String.valueOf(product.getProductSalePrice());
                })
                .collect(Collectors.toList());
    }


}
