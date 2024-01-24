package com.dmarket.service;

import com.dmarket.dto.response.UserInfoResDto;
import com.dmarket.dto.common.WishlistItemDto;
import com.dmarket.dto.response.WishlistResDto;
import com.dmarket.repository.user.UserRepository;
import com.dmarket.repository.user.WishlistRepository;
import com.dmarket.domain.user.Cart;
import com.dmarket.domain.user.Wishlist;
import com.dmarket.repository.user.CartRepository;
import com.dmarket.repository.user.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    //조회가 아닌 메서드들은 꼭 @Transactional 넣어주세요 (CUD, 입력/수정/삭제)
    private final CartRepository cartRepository;
    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;

    // 장바구니 추가
    @Transactional
    public void addCart(Long userId, Long productId, Long optionId, Integer productCount) {
        // 위시리스트 저장
        Cart cart = Cart.builder()
                .userId(userId)
                .productId(productId)
                .optionId(optionId)
                .cartCount(productCount)
                .build();
        cartRepository.save(cart);
    }

    // 위시리스트 추가
    @Transactional
    public void addWish(Long userId, Long productId) {
        // 위시리스트 저장
        Wishlist wishlist = Wishlist.builder()
                .userId(userId)
                .productId(productId)
                .build();
        wishlistRepository.save(wishlist);
    }

    // 위시리스트 조회
    public WishlistResDto getWishlistByUserId(Long userId) {
        List<WishlistItemDto> wishlistItems = wishlistRepository.findWishlistItemsByUserId(userId);
        return WishlistResDto.builder()
                .wishListItem(wishlistItems)
                .build();
    }

    // 사용자 정보 조회
    public UserInfoResDto getUserInfoByUserId(Long userId) {
        return userRepository.findUserInfoByUserId(userId);
    }

    // 위시리스트 삭제
    @Transactional
    public void deleteWishlistById(Long wishlistId) {
        wishlistRepository.deleteById(wishlistId);
    }
}
