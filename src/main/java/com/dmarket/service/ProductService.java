package com.dmarket.service;

import com.dmarket.dto.response.CategoryListResDto;
import com.dmarket.repository.product.CategoryRepository;
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

    //private static final int PAGE_POST_COUNT = 16;

    // 카테고리 전체 목록 depth별로 조회
    @Transactional(readOnly = true)
    public List<CategoryListResDto> getCategories(Integer categoryDepthLevel){
        return categoryRepository.findByCategoryDepth(categoryDepthLevel);
    }
}
