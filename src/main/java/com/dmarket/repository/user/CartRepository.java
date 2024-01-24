package com.dmarket.repository.user;

import com.dmarket.domain.user.Cart;
import com.dmarket.dto.response.CartCountResDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    // 장바구니 상품 개수 조회
    @Query(value = "select new com.dmarket.dto.response.CartCountResDto" +
                    "(COUNT(c.cartId) as cartCount) from Cart c where userId = :userId")
    CartCountResDto findCountByUserId(Long userId);
}
