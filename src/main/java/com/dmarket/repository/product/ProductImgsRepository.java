package com.dmarket.repository.product;

import com.dmarket.domain.product.ProductImgs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImgsRepository extends JpaRepository<ProductImgs, Long> {
    @Query("select pi.imgAddress from ProductImgs pi where pi.productId = :productId")
    List<String> findAllByProductId(Long productId);
}
