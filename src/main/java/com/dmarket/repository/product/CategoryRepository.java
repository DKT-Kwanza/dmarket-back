package com.dmarket.repository.product;

import com.dmarket.domain.product.Category;
import com.dmarket.domain.product.Product;
import com.dmarket.dto.common.ProductCommonDto;
import com.dmarket.dto.response.CategoryResDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "select c from Category c where c.categoryDepth = :categoryDepth")
    List<CategoryResDto.CategoryListResDto> findByCategoryDepth(Integer categoryDepth);

    // 상품의 카테고리 1, 2 조회
    Category findByCategoryId(Long categoryId);

    Category findByCategoryName(String categoryName);

    // 상품 옵션 조회 status 추가




    // 상품 옵션 조회 status 추가
//    @Query(value = "select new.com.dmarket.dto.common.ProductCommonDto.ProductOptionListDto(o)" +
//            " from Category c" +
//            " join Product p on c.categoryId = p.categoryId" +
//            " join ProductOption o on p.productId = o.productId" +
//            " where c.categoryId = :categoryId")
    @Query(value = "select new com.dmarket.dto.common.ProductCommonDto$ProductOptionListDto(o) " +
            "from Category c " +
            "join Product p on c.categoryId = p.categoryId " +
            "join ProductOption o on p.productId = o.productId " +
            "where o.productId = :productId")
    List<ProductCommonDto.ProductOptionListDto> findOptionsByCategoryId(@Param("productId") Long productId);

    @Query(value = "select p from Product p where p.categoryId = :categoryId" +
            " order by p.productCreatedDate desc")
    List<Product> findProductsByCategoryId(@Param("categoryId") Long categoryId);

    @Query(value = "select pi.imgAddress from Category c" +
            " join Product p on c.categoryId = p.categoryId" +
            " join ProductImgs pi on p.productId = pi.productId" +
            " where c.categoryId = :categoryId")
    List<String> findImgsByCategoryId(@Param("categoryId") Long categoryId);
}
