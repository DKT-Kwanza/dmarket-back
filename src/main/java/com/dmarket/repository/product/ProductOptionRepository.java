package com.dmarket.repository.product;

import com.dmarket.domain.product.ProductOption;
import com.dmarket.dto.common.ProductCommonDto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

    // 상품의 옵션 목록 조회
    @Query("select new com.dmarket.dto.common.ProductCommonDto$ProductOptionDto(o) " +
            "from ProductOption o where o.productId = :productId")
    List<ProductCommonDto.ProductOptionDto> findOptionsByProductId(Long productId);

    // 상품의 옵션 삭제
    void deleteByOptionId(@Param("optionId") Long optionId);

    void deleteByProductId(@Param("productId") Long productId);

    List<ProductOption> findOptionsByProductIdIn(List<Long> productIds);

    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM ProductOption o WHERE o.productId = :productId")
    boolean existsByProductId(@Param("productId") Long productId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from ProductOption o where o.optionId = :productOptionId")
    Optional<ProductOption> findByIdForUpdate(Long productOptionId);
}
