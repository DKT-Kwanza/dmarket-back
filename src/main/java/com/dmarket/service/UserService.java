package com.dmarket.service;

import com.dmarket.domain.user.User;
import com.dmarket.dto.request.ChangePwdReqDto;
import com.dmarket.dto.response.CartCountResDto;
import com.dmarket.dto.response.UserHeaderInfoResDto;
import com.dmarket.dto.response.UserInfoResDto;
import com.dmarket.dto.common.WishlistItemDto;
import com.dmarket.dto.response.WishlistResDto;
import com.dmarket.jwt.JWTUtil;
import com.dmarket.repository.user.CartRepository;
import com.dmarket.repository.user.UserRepository;
import com.dmarket.repository.user.WishlistRepository;
import com.dmarket.domain.user.Cart;
import com.dmarket.domain.user.Wishlist;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    // 사용자 정보 조회
    public UserInfoResDto getUserInfoByUserId(Long userId) {
        return userRepository.findUserInfoByUserId(userId);
    }

    // 위시리스트 조회
    public WishlistResDto getWishlistByUserId(Long userId) {
        List<WishlistItemDto> wishlistItems = wishlistRepository.findWishlistItemsByUserId(userId);
        return WishlistResDto.builder()
                .wishListItem(wishlistItems)
                .build();
    }

    //장바구니 상품 개수 조회
    public CartCountResDto getCartCount(Long userId){
        return cartRepository.findCountByUserId(userId);
    }

    // 마이페이지 서브헤더 사용자 정보 및 마일리지 조회
    public UserHeaderInfoResDto getSubHeader(Long userId){
        return userRepository.findUserHeaderInfoByUserId(userId);
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

    // 위시리스트 삭제
    @Transactional
    public void deleteWishlistById(Long wishlistId) {
        wishlistRepository.deleteById(wishlistId);
    }

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

    //사용자 비밀번호 확인
    @Transactional
    public void validatePassword(HttpServletRequest request, String currentPassword){
        String header = jwtUtil.getAuthHeader(request);
        String token = jwtUtil.getToken(header);

        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        String email = jwtUtil.getEmail(token);
        User findUser = userRepository.findByUserEmail(email);
        if (!isPasswordSame(currentPassword, findUser.getUserPassword())) {
            throw new IllegalArgumentException("비밀번호가 다릅니다.");
        }
    }

    //사용자 비밀번호 변경
    @Transactional
    public void updatePassword(String newPassword, Long userId){
        User user = findById(userId);
        if (isPasswordSame(newPassword, user.getUserPassword())) {
            throw new IllegalArgumentException("동일한 비밀번호입니다.");
        } else{
            user.updatePassword(passwordEncoder.encode(newPassword));
        }
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 id 입니다."));
    }
    public boolean isPasswordSame(String pwd, String originPwd) {
        return passwordEncoder.matches(pwd, originPwd);
    }


}
