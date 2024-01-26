package com.dmarket.repository.product;

import com.dmarket.domain.product.Product;
import com.dmarket.dto.common.ProductListDto;
import com.dmarket.dto.response.NewProductResDto;
import com.dmarket.dto.response.RecommendProductResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.dmarket.dto.common.ProductDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    //카테고리별 상품 번호, 브랜드, 이름, 대표이미지, 판매가, 평점, 리뷰개수 조회
    @Query(value = "select new com.dmarket.dto.common.ProductListDto" +
            "(p.productId, p.productBrand, p.productName, MIN(i.imgAddress) as productImg, " +
            "p.productSalePrice, p.productRating, COUNT(DISTINCT r.reviewId) as reviewCnt) " +
            "from Product p " +
            "join Category c on c.categoryId = p.categoryId " +
            "left join ProductReview r on r.productId = p.productId " +
            "left join ProductImgs i on i.productId = p.productId " +
            "where p.categoryId = :cateId and p.productSalePrice between :minPrice and :maxPrice " +
            "and CASE WHEN (:star = 0) THEN " +
            "(p.productRating >= :star or p.productRating IS NULL) " +
            "ELSE p.productRating >= :star END " +
            "group by p.productId")
    Page<ProductListDto> findByCateId(Pageable pageable, Long cateId, Integer minPrice, Integer maxPrice, Float star);

    // 상품 이름 or 브랜드로 목록 검색
    @Query(value = "select new com.dmarket.dto.common.ProductListDto" +
            "(p.productId, p.productBrand, p.productName, MIN(i.imgAddress) as productImg, " +
            "p.productSalePrice, p.productRating, COUNT(DISTINCT r.reviewId) as reviewCnt) " +
            "from Product p " +
            "join Category c on c.categoryId = p.categoryId " +
            "left join ProductReview r on r.productId = p.productId " +
            "left join ProductImgs i on i.productId = p.productId " +
            "where (p.productName LIKE %:query% or p.productBrand LIKE %:query%) " +
            "and p.productSalePrice between :minPrice and :maxPrice " +
            "and CASE WHEN (:star = 0) THEN " +
            "(p.productRating >= :star or p.productRating IS NULL) " +
            "ELSE p.productRating >= :star END " +
            "group by p.productId")
    Page<ProductListDto> findByQuery(Pageable pageable, String query, Integer minPrice, Integer maxPrice, Float star);

    // 상품의 상품 번호, 평균 평점, 리뷰 개수 조회
    @Query("select new com.dmarket.dto.common.ProductDto" +
            "(p.productId, p.productRating, count(r.reviewId)) " +
            "from Product p " +
            "left join ProductReview r on p.productId = r.productId " +
            "where p.productId = :productId " +
            "group by p.productId")
    Optional<ProductDto> findProductByProductId(Long productId);

    // 같은 카테고리의 추천 상품(최신순 4개) 검색
    @Query("select new com.dmarket.dto.response.RecommendProductResDto" +
            "(p.categoryId, p.productId, p.productBrand, p.productName, p.productSalePrice, p.productRating, count(r.reviewId)) " +
            "from Product p " +
            "left join ProductReview r on p.productId = r.productId " +
            "where p.productId != :productId and p.categoryId = (select sp.categoryId from Product sp where sp.productId = :productId) " +
            "group by p.productId")
    List<RecommendProductResDto> findProduct(Long productId, Pageable pageable);

    // 최신 상품 조회
    @Query("SELECT NEW com.dmarket.dto.response.NewProductResDto(" +
            "p.productId, p.productBrand, p.productName, MIN(pi.imgAddress), p.productSalePrice) " +
            "FROM Product p " +
            "LEFT JOIN ProductImgs pi ON p.productId = pi.productId " +
            "GROUP BY p.productId " +
            "ORDER BY p.productCreatedDate DESC")
    List<NewProductResDto> findNewProducts();


        @Modifying
        @Transactional
        @Query("UPDATE Product p SET p.categoryId = :categoryId, p.productBrand = :productBrand, " +
                        "p.productName = :productName, p.productPrice = :productPrice, " +
                        "p.productSalePrice = :productSalePrice, p.productDescription = :productDescription " +
                        "WHERE p.productId = :productId")
        void updateProductDetails(@Param("productId") Long productId,
                        @Param("categoryId") Long categoryId,
                        @Param("productBrand") String productBrand,
                        @Param("productName") String productName,
                        @Param("productPrice") Integer productPrice,
                        @Param("productSalePrice") Integer productSalePrice,
                        @Param("productDescription") String productDescription);
}
