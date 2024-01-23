package com.dmarket.service;

import com.dmarket.dto.response.WishlistItemDto;
import com.dmarket.dto.response.WishlistResDto;
import com.dmarket.repository.user.UserRepository;
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
    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    // 위시리스트 조회
    @Transactional(readOnly = true)
    public WishlistResDto getWishlistByUserId(Long userId) {
        List<WishlistItemDto> wishlistItems = wishlistRepository.findWishlistItemsByUserId(userId);
        return WishlistResDto.builder()
                .wishCount(wishlistItems.size()) // Set wishCount here
                .wishListItem(wishlistItems)
                .build();
    }
}
