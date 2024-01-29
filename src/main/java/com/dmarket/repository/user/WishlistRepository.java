package com.dmarket.repository.user;

import com.dmarket.domain.user.Wishlist;
import com.dmarket.dto.common.WishlistItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    // 위시리스트 존재 여부 확인
    Boolean existsByUserIdAndProductId(Long userId, Long productId);

    @Query(value = "select new com.dmarket.dto.common.WishlistItemDto(" +
            "   p.productId, w.wishlistId, p.productName, p.productBrand, MIN(pi.imgAddress), p.productSalePrice)" +
            " from Wishlist w" +
            " join Product p on w.productId = p.productId " +
            " join ProductImgs pi on p.productId = pi.productId " +
            " where w.userId = :userId" +
            " group by p.productId, w.wishlistId, p.productName, p.productBrand, p.productSalePrice" +
            " order by w.wishlistId desc")
    Page<WishlistItemDto> findWishlistItemsByUserId(Pageable pageable, @Param("userId") Long userId);

    void deleteById(@Param("wishlistId") Long wishlistId);
}
