package com.dmarket.repository.user;

import com.dmarket.domain.user.Cart;
import com.dmarket.dto.common.CartCommonDto;
import com.dmarket.dto.response.CartResDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // 장바구니 상품 개수 조회
    @Query(value = "select new com.dmarket.dto.response.CartResDto$CartCountResDto" +
            "(COUNT(c.cartId) as cartCount) from Cart c where userId = :userId")
    CartResDto.CartCountResDto findCountByUserId(Long userId);

    @Query("SELECT new com.dmarket.dto.common.CartCommonDto$CartListDto(c, p, o, pi) " +
            "FROM Cart c " +
            "JOIN Product p ON p.productId = c.productId " +
            "JOIN ProductOption o ON o.optionId = c.optionId " +
            "JOIN ProductImgs pi ON pi.productId = c.productId " +
            "WHERE c.userId = :userId AND pi.imgId = (" +
            "SELECT MIN(pi2.imgId) FROM ProductImgs pi2 WHERE pi2.productId = c.productId" +
            ")")
    List<CartCommonDto.CartListDto> getCartsfindByUserId(@Param("userId") Long userId);

    void deleteByCartId(@Param("cartId") Long cartId);

    void deleteByOptionId(@Param("optionId") Long optionId);
 
    Optional<Cart> findByUserIdAndOptionId(Long userId, Long optionId);
}
