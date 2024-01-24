package com.dmarket.service;

import com.dmarket.domain.board.Inquiry;
import com.dmarket.dto.response.UserInfoResDto;
import com.dmarket.dto.common.WishlistItemDto;
import com.dmarket.dto.response.WishlistResDto;
import com.dmarket.repository.board.InquiryRepository;
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
    private final InquiryRepository inquiryRepository;

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



    // 문의 작성
    @Transactional
    public Inquiry createInquiry(Inquiry inquiry) {
        try {
            return inquiryRepository.save(inquiry);
        } catch (Exception e) {
            log.error("문의 작성 실패 로그: {}", e.getMessage());
            throw new RuntimeException("문의 작성 실패", e);
        }
    }
}
