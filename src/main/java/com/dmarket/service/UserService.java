package com.dmarket.service;

import com.dmarket.domain.board.Inquiry;
import com.dmarket.domain.board.Inquiry;
import com.dmarket.dto.common.WishlistItemDto;
import com.dmarket.repository.board.InquiryRepository;
import com.dmarket.repository.user.CartRepository;
import com.dmarket.repository.user.UserRepository;
import com.dmarket.repository.user.WishlistRepository;
import com.dmarket.domain.user.Cart;
import com.dmarket.domain.user.Wishlist;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

import com.dmarket.domain.order.Order;
import com.dmarket.dto.common.CartListDto;
import com.dmarket.dto.response.*;
import com.dmarket.repository.board.*;
import com.dmarket.repository.order.OrderDetailRepository;
import com.dmarket.repository.order.OrderRepository;
import com.dmarket.repository.product.QnaRepository;
import com.dmarket.repository.user.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final QnaRepository qnaRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartRepository cartRepository;
    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    // 조회가 아닌 메서드들은 꼭 @Transactional 넣어주세요 (CUD, 입력/수정/삭제)

    public List<CartListDto> getCartsfindByUserId(Long userId) {
        List<CartListDto> originalList = cartRepository.getCartsfindByUserId(userId);
        return originalList;
    }

    @Transactional
    public void deleteCartByCartId(Long userId, Long cartId) {
        cartRepository.deleteByCartId(cartId);

    }

    public Page<QnaResDto> getQnasfindByUserId(Long userId, Pageable pageable) {
        return qnaRepository.getQnasfindByUserId(userId, pageable);
    }

    public Page<OrderResDto> getOrderDetailsWithoutReviewByUserId(Long userId, Pageable pageable) {
        List<OrderResDto> orderResDtos = new ArrayList<>();

        Page<Order> ordersPage = orderRepository.findByUserIdOrderedByOrderIdDesc(userId, pageable);
        for (Order order : ordersPage) {
            List<OrderDetailResDto> orderDetailResDtos = orderDetailRepository
                    .findOrderDetailsWithoutReviewByOrder(order.getOrderId());
            if (!orderDetailResDtos.isEmpty()) {
                orderResDtos.add(new OrderResDto(order, orderDetailResDtos));
            }
        }
        // PageImpl을 사용하여 List를 Page로 변환합니다.
        return new PageImpl<>(orderResDtos, pageable, ordersPage.getTotalElements());
    }

    public Page<OrderResDto> getOrderDetailsWithReviewByUserId(Long userId, Pageable pageable) {
        List<OrderResDto> orderResDtos = new ArrayList<>();

        Page<Order> ordersPage = orderRepository.findByUserIdOrderedByOrderIdDesc(userId, pageable);
        for (Order order : ordersPage) {
            List<ReviewResDto> orderDetailResDtos = orderDetailRepository
                    .findOrderDetailsWithReviewByOrder(order.getOrderId());
            if (!orderDetailResDtos.isEmpty()) {
                orderResDtos.add(new OrderResDto(order, orderDetailResDtos));
            }
        }

        // PageImpl을 사용하여 List를 Page로 변환합니다.
        return new PageImpl<>(orderResDtos, pageable, ordersPage.getTotalElements());
    }

    // 사용자 정보 조회
    public UserInfoResDto getUserInfoByUserId(Long userId) {
        return userRepository.findUserInfoByUserId(userId);
    }

    private final InquiryRepository inquiryRepository;

    // 위시리스트 조회
    public WishlistResDto getWishlistByUserId(Long userId) {
        List<WishlistItemDto> wishlistItems = wishlistRepository.findWishlistItemsByUserId(userId);
        return WishlistResDto.builder()
                .wishListItem(wishlistItems)
                .build();
    }

    // 장바구니 상품 개수 조회
    public CartCountResDto getCartCount(Long userId) {
        return cartRepository.findCountByUserId(userId);
    }

    // 마이페이지 서브헤더 사용자 정보 및 마일리지 조회
    public UserHeaderInfoResDto getSubHeader(Long userId) {
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

}
