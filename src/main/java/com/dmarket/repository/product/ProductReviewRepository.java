package com.dmarket.repository.product;

import com.dmarket.domain.product.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    Long countByProductId(Long productId);
}
