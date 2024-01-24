package com.dmarket.repository.product;

import com.dmarket.domain.product.Product;
import com.dmarket.dto.common.ProductDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 상품의 상품 번호, 평균 평점, 리뷰 개수 조회
    @Query("select new com.dmarket.dto.common.ProductDto" +
            "(p.productId, p.productRating, count(r.reviewId)) " +
            "from Product p " +
            "join ProductReview r on p.productId = r.productId " +
            "where p.productId = :productId " +
            "group by p.productId")
    Optional<ProductDto> findProductByProductId(Long productId);
}
