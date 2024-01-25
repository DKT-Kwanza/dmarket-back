package com.dmarket.service;

import com.dmarket.domain.board.Inquiry;
import com.dmarket.domain.user.Cart;
import com.dmarket.domain.user.User;
import com.dmarket.domain.user.Wishlist;
import com.dmarket.dto.common.WishlistItemDto;
import com.dmarket.dto.request.JoinReqDto;
import com.dmarket.repository.user.UserRepository;
import com.dmarket.dto.response.CartCountResDto;
import com.dmarket.dto.response.UserHeaderInfoResDto;
import com.dmarket.dto.response.UserInfoResDto;
import com.dmarket.dto.response.WishlistResDto;
import com.dmarket.repository.board.InquiryRepository;
import com.dmarket.repository.user.CartRepository;
import com.dmarket.repository.user.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

import com.dmarket.domain.order.Order;
import com.dmarket.dto.common.CartListDto;
import com.dmarket.dto.response.*;
import com.dmarket.repository.order.OrderDetailRepository;
import com.dmarket.repository.order.OrderRepository;
import com.dmarket.repository.product.QnaRepository;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    //조회가 아닌 메서드들은 꼭 @Transactional 넣어주세요 (CUD, 입력/수정/삭제)

    private final QnaRepository qnaRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartRepository cartRepository;
    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final RedisService redisService;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;
    private static final String AUTH_CODE_PREFIX = "auth:email:";

    /**
     * 회원가입
     * @Return userId
     */
    @Transactional
    public Long join(JoinReqDto dto) {

        User user = User.builder()
                .userEmail(dto.getUserEmail())
                .password(passwordEncoder.encode(dto.getUserPassword()))
                .userDktNum(dto.getUserDktNum())
                .userName(dto.getUserName())
                .userPhoneNum(dto.getUserPhoneNum())
                .userJoinDate(dto.getUserJoinDate())
                .userPostalCode(dto.getUserPostalCode())
                .userAddress(dto.getUserAddress())
                .userAddressDetail(dto.getUserDetailedAddress())
                .build();

        userRepository.save(user);

        return user.getUserId();
    }

    //회원가입 유효성 확인
    public void verifyJoin(JoinReqDto dto) {

        String regExp = "^[a-zA-Z0-9!@#$%^]*$";
        String userEmail = dto.getUserEmail();
        String password = dto.getUserPassword();
        Integer userDktNum = dto.getUserDktNum();

        isValidEmail(userEmail);

        //비밀번호 특수문자 모두 포함
        if (!password.matches(regExp)) {
            throw new IllegalArgumentException("비밀번호는 영문자, 숫자, 특수문자(!@#$%^)가 포함되어야 합니다.");
        }

        //사원번호 겹치면 안됨
        if (existByUserDktNum(userDktNum)) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
    }

    public void isValidEmail(String email) {

        //이메일이 gachon.ac.kr로 끝나야 함
        if (!email.endsWith("gachon.ac.kr")) {
            throw new IllegalArgumentException("이메일이 gachon.ac.kr로 끝나지 않습니다.");
        }
        //이메일이 겹치면 안 됨
        if (existByUserEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
    }

    public void sendCodeToEmail(String toEmail) {
        isValidEmail(toEmail);
        String title = "Dmarket 회원가입 인증번호";
        String authCode = createCode();
        mailService.sendEmail(toEmail, title, authCode);

        // 이메일 인증 요청 시 인증 번호 Redis에 저장 ( key = auth:email:abc@gachon.ac.kr / value = 000000 )
        redisService.setValues(AUTH_CODE_PREFIX + toEmail, authCode, Duration.ofMillis(this.authCodeExpirationMillis));
    }

    // Redis 구현 후 완성
    public void isValidEmailCode(String email, String authCode) {
        isValidEmail(email);
        String redisAuthCode = redisService.getValues(AUTH_CODE_PREFIX + email);

        //Redis에서 갖고 온 값이 null이 아니고 이메일 인증 코드가 동일하면 true
        boolean isValid = redisService.checkExistsValue(redisAuthCode) && redisAuthCode.equals(authCode);

        if (!isValid) {
            throw new IllegalArgumentException("인증 코드가 동일하지 않습니다.");
        }
    }


    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    public boolean existByUserEmail(String userEmail) {
        return userRepository.existsByUserEmail(userEmail);
    }

    public boolean existByUserDktNum(Integer userDktNum) {
        return userRepository.existsByUserDktNum(userDktNum);
    }


    private String createCode() {
        int length = 6;
        try {
            SecureRandom random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            log.warn("UserService.createCode()");
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }



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

}
