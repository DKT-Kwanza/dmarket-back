package com.dmarket.repository.product;

import com.dmarket.domain.product.Category;
import com.dmarket.dto.response.CategoryListResDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "select c from Category c where c.categoryDepth = :categoryDepth")
    List<CategoryListResDto> findByCategoryDepth(Integer categoryDepth);

    // 상품의 카테고리 1, 2 조회
    Category findByCategoryId(Long categoryId);

    
    Category findByCategoryName(String categoryName);
}

