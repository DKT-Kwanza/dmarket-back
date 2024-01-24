package com.dmarket.repository.product;

import com.dmarket.domain.product.ProductOption;
import com.dmarket.dto.ProductOptionDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    @Query("select new com.dmarket.dto.ProductOptionDto" +
            "(o.optionId, o.optionName, o.optionValue, o.optionQuantity) " +
            "from ProductOption o where o.productId = :productId")
    List<ProductOptionDto> findOptionsByProductId(Long productId);
}
