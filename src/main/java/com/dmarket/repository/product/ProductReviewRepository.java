package com.dmarket.repository.product;

import com.dmarket.domain.product.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    void deleteByReviewId(@Param("reviewId") Long reviewId);
}
