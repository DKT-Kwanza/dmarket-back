package com.dmarket.service;

import com.dmarket.constant.MileageReqState;
import com.dmarket.constant.MileageReqType;
import com.dmarket.domain.board.Inquiry;
import com.dmarket.domain.user.MileageReq;
import com.dmarket.dto.response.CartCountResDto;
import com.dmarket.dto.response.UserHeaderInfoResDto;
import com.dmarket.dto.response.UserInfoResDto;
import com.dmarket.dto.common.WishlistItemDto;
import com.dmarket.dto.response.WishlistResDto;
import com.dmarket.repository.board.InquiryRepository;
import com.dmarket.repository.user.CartRepository;
import com.dmarket.repository.user.UserRepository;
import com.dmarket.repository.user.WishlistRepository;
import com.dmarket.domain.user.Cart;
import com.dmarket.domain.user.Wishlist;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

import com.dmarket.domain.order.Order;
import com.dmarket.dto.common.CartListDto;
import com.dmarket.dto.response.*;
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
    private final MileageReqRepository mileageReqRepository;
    // 조회가 아닌 메서드들은 꼭 @Transactional 넣어주세요 (CUD, 입력/수정/삭제)


    public List<CartListDto> getCartsfindByUserId(Long userId) {
        List<CartListDto> originalList = cartRepository.getCartsfindByUserId(userId);
        return originalList;
    }

    @Transactional
    public void deleteCartByCartId(Long userId, Long cartId) {
        cartRepository.deleteByCartId(cartId);

    }

    public List<QnaResDto> getQnasfindByUserId(Long userId) {
        return qnaRepository.getQnasfindByUserId(userId);
    }


    public List<OrderResDto> getOrderDetailsWithoutReviewByUserId(Long userId) {
        List<OrderResDto> orderResDtos = new ArrayList<>();

        List<Order> orders = orderRepository.findByUserId(userId);
        for (Order order : orders) {
            List<OrderDetailResDto> orderDetailResDtos = orderDetailRepository.findOrderDetailsWithoutReviewByOrder(order.getOrderId());
            if (!orderDetailResDtos.isEmpty()) {
                orderResDtos.add(new OrderResDto(order, orderDetailResDtos));
            }
        }

        return orderResDtos;
    }


    public List<OrderResDto> getOrderDetailsWithReviewByUserId(Long userId) {
        List<OrderResDto> orderResDtos = new ArrayList<>();

        List<Order> orders = orderRepository.findByUserId(userId);
        for (Order order : orders) {
            List<ReviewResDto> orderDetailResDtos = orderDetailRepository.findOrderDetailsWithReviewByOrder(order.getOrderId());
            if (!orderDetailResDtos.isEmpty()) {
                orderResDtos.add(new OrderResDto(order, orderDetailResDtos));
            }
        }

        return orderResDtos;
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

    // 마일리지 충전 요청
    @Transactional
    public void mileageChargeReq(Long userId, Integer mileageCharge) {
        // 마일리지 요청 사유 CHARGE(충전), 마일리지 요청 처리 상태(처리 중)으로 지정
        MileageReq mileageReq = MileageReq.builder()
                .userId(userId)
                .mileageReqAmount(mileageCharge)
                .mileageReqReason(MileageReqType.CHARGE)
                .mileageReqState(MileageReqState.PROCESSING)
                .build();

        mileageReqRepository.save(mileageReq);
    }
}
