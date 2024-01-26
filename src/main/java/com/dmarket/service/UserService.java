package com.dmarket.service;

import com.dmarket.constant.MileageReqState;
import com.dmarket.constant.MileageContents;
import com.dmarket.constant.OrderDetailState;
import com.dmarket.domain.user.User;
import com.dmarket.dto.common.*;
import com.dmarket.dto.request.UserAddressReqDto;
import com.dmarket.dto.response.*;
import com.dmarket.jwt.JWTUtil;
import com.dmarket.domain.board.Inquiry;
import com.dmarket.domain.user.Mileage;
import com.dmarket.domain.user.MileageReq;
import com.dmarket.domain.user.Cart;
import com.dmarket.domain.user.Wishlist;
import com.dmarket.dto.request.JoinReqDto;
import com.dmarket.repository.user.*;
import com.dmarket.repository.board.InquiryRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import com.dmarket.domain.order.Order;
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
    private final MileageRepository mileageRepository;
    private final MileageReqRepository mileageReqRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
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

        String regExp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^])(?=.*[0-9]).{8,25}$";
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

    // 페이지 사이즈
    private static final int PAGE_SIZE = 10;

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

    //사용자 배송지 변경
    @Transactional
    public UserAddressResDto updateAddress(HttpServletRequest request,
                              Long userId, UserAddressReqDto userAddressReqDto){
        String header = jwtUtil.getAuthHeader(request);
        String token = jwtUtil.getToken(header);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        String email = jwtUtil.getEmail(token);
        User user = userRepository.findByUserEmail(email);
        user.updateAddress(userAddressReqDto);
        return new UserAddressResDto(user);
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
        User user = userRepository.findByUserEmail(email);
        if (!isPasswordSame(currentPassword, user.getUserPassword())) {
            throw new IllegalArgumentException("비밀번호가 다릅니다.");
        }
    }

    //사용자 비밀번호 변경
    @Transactional
    public void updatePassword(String newPassword, Long userId){
        String regExp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^])(?=.*[0-9]).{8,25}$";
        //비밀번호 유효성 검사
        if (!newPassword.matches(regExp)) {
            throw new IllegalArgumentException("비밀번호는 영문자, 숫자, 특수문자(!@#$%^)가 포함되어야 합니다.");
        }
        User user = findById(userId);
        if (isPasswordSame(newPassword, user.getUserPassword())) {
            throw new IllegalArgumentException("동일한 비밀번호입니다.");
        } else{
            user.updatePassword(passwordEncoder.encode(newPassword));
        }
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 회원 입니다."));
    }
    public boolean isPasswordSame(String pwd, String originPwd) {
        return passwordEncoder.matches(pwd, originPwd);
    }

    public boolean existByUserEmail(String userEmail) {
        return userRepository.existsByUserEmail(userEmail);
    }

    public boolean existByUserDktNum(Integer userDktNum) {
        return userRepository.existsByUserDktNum(userDktNum);
    }

    // 마일리지 사용(충전) 내역 조회
    public MileageListResDto getMileageUsage(Long userId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "mileageDate"));
        Page<Mileage> mileages = mileageRepository.findByUserId(pageable, userId);

        List<MileageDto> mileageChageList = mileages.getContent().stream().map(
                (o) -> MileageDto.builder()
                        .mileageChangeDate(o.getMileageDate())
                        .mileageContents(o.getMileageInfo())
                        .changeMileage(o.getChangeMileage())
                        .remainMileage(o.getRemainMileage())
                        .build()).collect(Collectors.toList());

        return new MileageListResDto(mileages.getTotalPages(), mileageChageList);
    }

    // 마일리지 충전 요청
    @Transactional
    public void mileageChargeReq(Long userId, Integer mileageCharge) {
        // 마일리지 요청 사유 CHARGE(충전), 마일리지 요청 처리 상태(처리 중)으로 지정
        MileageReq mileageReq = MileageReq.builder()
                .userId(userId)
                .mileageReqAmount(mileageCharge)
                .mileageReqReason(MileageContents.CHARGE)
                .mileageReqState(MileageReqState.PROCESSING)
                .build();

        mileageReqRepository.save(mileageReq);
    }

    // 사용자 문의 전체 조회
    public List<UserInquiryAllResDto> getUserInquiryAllbyUserId(Long userId) {
        return inquiryRepository.findUserInquiryAllByUserId(userId);
    }

    // 사용자 주문 내역 상세 조회
    public OrderDetailListResDto getOrderDetailListByOrderId(Long orderId,Long userId) {
        List<ProductDetailListDto> productDetailList = orderDetailRepository.findOrderDetailByOrderId(orderId);
        Order order = orderRepository.findByOrderId(orderId);
        User user = userRepository.findByUserId(userId);
        return new OrderDetailListResDto(order, user, productDetailList);
    }

//    // 주문 / 배송 내역 조회
//    public OrderListResDto getOrderListResByUserId(Long userId){
//        List<OrderListDto> orderList = new ArrayList<>();
//        List<Order> orders = userRepository.findOrderByUserId(userId);
//        for (Order order : orders) {
//            List<ProductDetailListDto> productDetailListDtos = userRepository.findOrderDetailByUserId(userId);
//            orderList.add(new OrderListDto(order, productDetailListDtos));
//        }
//
//        return new OrderListResDto(confPaycount, preShipCount, inTransitCount, cmpltDilCount, orderCancelCount, returnCount, orderList);
//    }
    public OrderListResDto getOrderListResByUserId(Long userId) {
        List<OrderListDto> orderList = new ArrayList<>();
        List<Order> orders = orderRepository.findByUserId(userId);

        Long confPayCount = orderDetailRepository.countOrderDetailByUserIdAndOrderDetailState(userId, OrderDetailState.ORDER_COMPLETE);
        Long preShipCount = orderDetailRepository.countOrderDetailByUserIdAndOrderDetailState(userId, OrderDetailState.DELIVERY_READY);
        Long inTransitCount = orderDetailRepository.countOrderDetailByUserIdAndOrderDetailState(userId, OrderDetailState.DELIVERY_ING);
        Long cmpltDilCount = orderDetailRepository.countOrderDetailByUserIdAndOrderDetailState(userId, OrderDetailState.DELIVERY_COMPLETE);
        Long orderCancelCount = orderDetailRepository.countOrderDetailByUserIdAndOrderDetailState(userId, OrderDetailState.ORDER_CANCEL);
        Long returnCount = orderDetailRepository.countOrderDetailByUserIdAndOrderDetailState(userId, OrderDetailState.RETURN_REQUEST) +
                orderDetailRepository.countOrderDetailByUserIdAndOrderDetailState(userId, OrderDetailState.RETURN_COMPLETE);

        for (Order order : orders) {
            List<ProductDetailListDto> productDetailListDtos = orderDetailRepository.findOrderDetailByUserId(userId);
            orderList.add(new OrderListDto(order, productDetailListDtos));
        }

        OrderListResDto orderListResDto = new OrderListResDto();

        orderListResDto.setConfPayCount(confPayCount);
        orderListResDto.setPreShipCount(preShipCount);
        orderListResDto.setInTransitCount(inTransitCount);
        orderListResDto.setCmpltDilCount(cmpltDilCount);
        orderListResDto.setOrderCancelCount(orderCancelCount);
        orderListResDto.setReturnCount(returnCount);
        orderListResDto.setOrderList(orderList);

        return orderListResDto;
    }

}
