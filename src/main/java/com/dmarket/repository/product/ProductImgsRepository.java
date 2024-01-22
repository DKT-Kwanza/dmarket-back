package com.dmarket.repository.product;

import com.dmarket.domain.product.ProductImgs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImgsRepository extends JpaRepository<ProductImgs, Long> {
}
