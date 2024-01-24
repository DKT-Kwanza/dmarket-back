package com.dmarket.repository.product;

import com.dmarket.domain.product.ProductReview;
import com.dmarket.dto.common.ProductReviewDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    // 상품ID로 리뷰 개수 조회
    Long countByProductId(Long productId);

    // 상품 번호로 리뷰 목록 조회
    @Query("select new com.dmarket.dto.common.ProductReviewDto" +
            "(r.reviewId, u.userName, o.optionValue, r.reviewRating, r.reviewContents, r.reviewCreatedDate, r.reviewImg) " +
            "from ProductReview r " +
            "join User u on r.userId = u.userId " +
            "join ProductOption o on r.optionId = o.optionId " +
            "where r.productId = :productId")
    List<ProductReviewDto> findReviewByProductId(Long productId);

    void deleteByReviewId(@Param("reviewId") Long reviewId);
}
