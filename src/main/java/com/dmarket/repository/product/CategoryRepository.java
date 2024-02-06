package com.dmarket.repository.product;

import com.dmarket.domain.product.Category;
import com.dmarket.dto.response.CategoryResDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "select c from Category c where c.categoryDepth = :categoryDepth")
    List<CategoryResDto.CategoryListResDto> findByCategoryDepth(Integer categoryDepth);

    // 상품의 카테고리 1, 2 조회
    Category findByCategoryId(Long categoryId);

    Category findIdByCategoryName(String categoryName);

    @Query(value = "SELECT c.categoryId FROM Category c WHERE c.categoryParentId = :parentId")
    List<Long> findCategoryIdByParentId(Long parentId);

    // 상품 옵션 조회 status 추가
//    @Query(value = "select new.com.dmarket.dto.common.ProductCommonDto.ProductOptionListDto(o)" +
//            " from Category c" +
//            " join Product p on c.categoryId = p.categoryId" +
//            " join ProductOption o on p.productId = o.productId" +
//            " where c.categoryId = :categoryId")
}
