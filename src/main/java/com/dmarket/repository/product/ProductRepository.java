package com.dmarket.repository.product;

import com.dmarket.domain.product.Product;
import com.dmarket.dto.response.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    //카테고리별 상품 번호, 브랜드, 이름, 대표이미지, 판매가, 평점, 리뷰개수 조회
    @Query(value = "select new com.dmarket.dto.response.ProductDto" +
            "(p.productId, p.productBrand, p.productName, MIN(i.imgAddress) as productImg, " +
            "p.productSalePrice, p.productRating, COUNT(DISTINCT r.reviewId) as reviewCnt) " +
            "from Product p " +
            "join Category c on c.categoryId = p.categoryId " +
            "left join ProductReview r on r.productId = p.productId " +
            "left join ProductImgs i on i.productId = p.productId " +
            "where p.categoryId = :cateId and p.productSalePrice between :minPrice and :maxPrice and p.productRating >= :star " +
            "group by p.productId")
    Page<ProductDto> findByCateId(Pageable pageable, Long cateId, Integer minPrice, Integer maxPrice, Float star);

    // 상품 이름으로 목록 검색
    @Query(value = "select new com.dmarket.dto.response.ProductDto" +
            "(p.productId, p.productBrand, p.productName, MIN(i.imgAddress) as productImg, " +
            "p.productSalePrice, p.productRating, COUNT(DISTINCT r.reviewId) as reviewCnt) " +
            "from Product p " +
            "join Category c on c.categoryId = p.categoryId " +
            "left join ProductReview r on r.productId = p.productId " +
            "left join ProductImgs i on i.productId = p.productId " +
            "where p.productName LIKE %:query% " +
            "or p.productBrand LIKE %:query% " +
            "and p.productSalePrice between :minPrice and :maxPrice and p.productRating >= :star " +
            "group by p.productId")
    Page<ProductDto> findByQuery(Pageable pageable, String query, Integer minPrice, Integer maxPrice, Float star);
}
