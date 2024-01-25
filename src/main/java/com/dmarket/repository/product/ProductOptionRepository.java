package com.dmarket.repository.product;

import com.dmarket.domain.product.ProductOption;
import com.dmarket.dto.common.ProductOptionDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    // 상품의 옵션 목록 조회
    @Query("select new com.dmarket.dto.common.ProductOptionDto" +
            "(o.optionId, o.optionName, o.optionValue, o.optionQuantity) " +
            "from ProductOption o where o.productId = :productId")
    List<ProductOptionDto> findOptionsByProductId(Long productId);

    void deleteByProductId(@Param("productId") Long productId);

}
