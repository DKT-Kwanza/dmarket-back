package com.dmarket.service;

import com.dmarket.constant.MileageContents;
import com.dmarket.constant.MileageReqState;
import com.dmarket.constant.OrderDetailState;
import com.dmarket.constant.ReturnState;
import com.dmarket.domain.board.Inquiry;
import com.dmarket.domain.order.Order;
import com.dmarket.domain.order.OrderDetail;
import com.dmarket.domain.order.Return;
import com.dmarket.domain.user.*;
import com.dmarket.dto.common.*;
import com.dmarket.dto.request.UserReqDto;
import com.dmarket.dto.response.*;
import com.dmarket.exception.BadRequestException;
import com.dmarket.exception.ConflictException;
import com.dmarket.exception.NotFoundException;
import com.dmarket.jwt.JWTUtil;
import com.dmarket.repository.board.InquiryRepository;
import com.dmarket.repository.order.OrderDetailRepository;
import com.dmarket.repository.order.OrderRepository;
import com.dmarket.repository.order.ReturnRepository;
import com.dmarket.repository.product.QnaRepository;
import com.dmarket.repository.user.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.dmarket.exception.ErrorCode.*;

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
    private final ReturnRepository returnRepository;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;
    private static final String AUTH_CODE_PREFIX = "auth:email:";

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int REVIEW_PAGE_SIZE = 5;

    /**
     * 회원가입
     */
    @Transactional
    public Long join(UserReqDto.Join dto) {

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
    public void verifyJoin(UserReqDto.Join dto) {

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
            throw new ConflictException(ALREADY_SAVED_USER);
        }
    }

    //이메일 유효성 검사
    public void isValidEmail(String email) {
        //이메일이 gachon.ac.kr로 끝나야 함
        if (!email.endsWith("gachon.ac.kr")) {
            throw new IllegalArgumentException("이메일이 gachon.ac.kr로 끝나지 않습니다.");
        }
        //이메일이 겹치면 안 됨
        if (existByUserEmail(email)) {
            throw new ConflictException(ALREADY_SAVED_USER);
        }
    }

    //이메일 인증 코드 발송
    public void sendCodeToEmail(String toEmail) {
        isValidEmail(toEmail);
        String title = "Dmarket 회원가입 인증번호";
        String authCode = createCode();
        mailService.sendEmail(toEmail, title, authCode);

        // 이메일 인증 요청 시 인증 번호 Redis에 저장 ( key = auth:email:abc@gachon.ac.kr / value = 000000 )
        redisService.setValues(AUTH_CODE_PREFIX + toEmail, authCode, Duration.ofMillis(this.authCodeExpirationMillis));
    }

    //이메일 인증 코드 유효성 검사
    public void isValidEmailCode(String email, String authCode) {
        isValidEmail(email);
        String redisAuthCode = redisService.getValues(AUTH_CODE_PREFIX + email);

        //Redis에서 갖고 온 값이 null이 아니고 이메일 인증 코드가 동일하면 true
        boolean isValid = redisService.checkExistsValue(redisAuthCode) && redisAuthCode.equals(authCode);

        if (!isValid) {
            throw new BadRequestException(INVALID_EMAIL_CODE);
        }
    }

    //이메일 인증 코드 생성
    private String createCode() {
        int length = 6;
        try {
            SecureRandom random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) { //얘도 예외처리 빼야가
            log.warn("UserService.createCode()");
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    /**
     * 장바구니 (cart)
     */
    // 장바구니 상품 개수 조회
    public CartResDto.CartCountResDto getCartCount(Long userId) {
        return cartRepository.findCountByUserId(userId);
    }

    public List<CartCommonDto.CartListDto> getCartsFindByUserId(Long userId) {
        List<CartCommonDto.CartListDto> originalList = cartRepository.getCartsfindByUserId(userId);
        return originalList;
    }

    // 장바구니 추가
    @Transactional
    public void addCart(Long userId, Long productId, Long optionId, Integer productCount) {
        // 장바구니에 존재하는 상품과 옵션인지 확인
        Optional<Cart> existingCart = cartRepository.findByUserIdAndOptionId(userId, optionId);
        if (existingCart.isPresent()) {
            // 수량만 추가
            existingCart.get().updateCartCount(productCount);
        } else {
            // 장바구니에 저장
            Cart cart = Cart.builder()
                    .userId(userId)
                    .productId(productId)
                    .optionId(optionId)
                    .cartCount(productCount)
                    .build();
            cartRepository.save(cart);
        }
    }

    @Transactional
    public void deleteCartByCartId(Long cartId) {
        cartRepository.deleteByCartId(cartId);

    }

    //작성한 qna 조회
    public Page<QnaResDto.QnaTotalListResDto> getQnasfindByUserId(Long userId, int pageNo) {
        pageNo = pageVaildation(pageNo);
        Pageable pageable = PageRequest.of(pageNo, DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "qnaId"));
        return qnaRepository.getQnasfindByUserId(userId, pageable);
    }

    public Page<OrderResDto> getOrderDetailsWithoutReviewByUserId(Long userId, int pageNo) {
        List<OrderResDto> orderResDtos = new ArrayList<>();
        pageNo = pageVaildation(pageNo);
        Pageable pageable = PageRequest.of(pageNo, REVIEW_PAGE_SIZE);
        List<Order> ordersPage = orderRepository.findByUserIdOrderedByOrderIdDesc(userId);

        for (Order order : ordersPage) {
            List<OrderDetailResDto> orderDetailResDtos = orderDetailRepository
                    .findOrderDetailsWithoutReviewByOrder(order.getOrderId());
            if (!orderDetailResDtos.isEmpty()) {
                orderResDtos.add(new OrderResDto(order, orderDetailResDtos));
            }
        }
        // PageImpl을 사용하여 List를 Page로 변환합니다.
        return new PageImpl<>(orderResDtos);
    }

    public Page<OrderResDto> getOrderDetailsWithReviewByUserId(Long userId, int pageNo) {
        List<OrderResDto> orderResDtos = new ArrayList<>();
        pageNo = pageVaildation(pageNo);
        Pageable pageable = PageRequest.of(pageNo, REVIEW_PAGE_SIZE);
        List<Order> ordersPage = orderRepository.findByUserIdOrderedByOrderIdDesc(userId);

        for (Order order : ordersPage) {
            List<ReviewResDto> orderDetailResDtos = orderDetailRepository
                    .findOrderDetailsWithReviewByOrder(order.getOrderId());
            if (!orderDetailResDtos.isEmpty()) {
                orderResDtos.add(new OrderResDto(order, orderDetailResDtos));
            }
        }

        // PageImpl을 사용하여 List를 Page로 변환합니다.
        return new PageImpl<>(orderResDtos);
    }

    // 사용자 정보 조회
    public UserResDto.UserInfo getUserInfoByUserId(Long userId) {
        return userRepository.findUserInfoByUserId(userId);
    }

    private final InquiryRepository inquiryRepository;

    // 위시리스트 조회
    public WishResDto.WishlistResDto getWishlistByUserId(Long userId, int pageNo) {
        pageNo = pageVaildation(pageNo);
        Pageable pageable = PageRequest.of(pageNo, DEFAULT_PAGE_SIZE);
        Page<WishlistItemDto> wishlistItems = wishlistRepository.findWishlistItemsByUserId(pageable, userId);
        WishResDto.WishlistResDto wishlistResDto = new WishResDto.WishlistResDto();
        wishlistResDto.setWishCount(wishlistRepository.countByUserId(userId));
        wishlistResDto.setWishListItem(wishlistItems);
        return wishlistResDto;
    }

    // 마이페이지 서브헤더 사용자 정보 및 마일리지 조회
    public UserResDto.UserHeaderInfo getSubHeader(Long userId) {
        return userRepository.findUserHeaderInfoByUserId(userId);
    }

    // 위시리스트 추가
    @Transactional
    public void addWish(Long userId, Long productId) {
        // 위시리스트에 있는지 확인 -> 있으면 에러 처리
        Boolean isWish = wishlistRepository.existsByUserIdAndProductId(userId, productId);
        if (isWish) {
            throw new ConflictException(ALREADY_SAVED_WISH);
        }
        // 위시리스트 저장
        Wishlist wishlist = Wishlist.builder()
                .userId(userId)
                .productId(productId)
                .build();
        wishlistRepository.save(wishlist);
    }

    public WishResDto.IsWishResDto checkIsWish(Long userId, Long productId) {
        return new WishResDto.IsWishResDto(wishlistRepository.existsByUserIdAndProductId(userId, productId));
    }

    // 위시리스트 삭제
    @Transactional
    public void deleteWishlistById(Long wishlistId) {
        wishlistRepository.deleteById(wishlistId);
    }

    // 문의 작성
    @Transactional
    public Inquiry createInquiry(Inquiry inquiry) {
        return inquiryRepository.save(inquiry);
    }

    //사용자 배송지 변경
    @Transactional
    public UserResDto.UserAddress updateAddress(HttpServletRequest request,
                                                Long userId, UserReqDto.UserAddress userAddressDto) {
        String header = jwtUtil.getAuthHeader(request);
        String token = jwtUtil.getToken(header);

        String email = jwtUtil.getEmail(token);
        User user = userRepository.findByUserEmail(email);
        user.updateAddress(userAddressDto);
        return new UserResDto.UserAddress(user);
    }

    //사용자 비밀번호 확인
    @Transactional
    public User validatePassword(HttpServletRequest request, String currentPassword) {
        String header = jwtUtil.getAuthHeader(request);
        String token = jwtUtil.getToken(header);

        String email = jwtUtil.getEmail(token);
        User user = userRepository.findByUserEmail(email); //예외처리 해야함
        if (!isPasswordSame(currentPassword, user.getUserPassword())) {
            throw new BadRequestException(INVALID_USER_PASSWORD);
        }
        return user;
    }

    //사용자 비밀번호 변경
    @Transactional
    public void updatePassword(String newPassword, User user) {
        String regExp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^])(?=.*[0-9]).{8,25}$";
        //비밀번호 유효성 검사
        if (!newPassword.matches(regExp)) {
            throw new IllegalArgumentException("비밀번호는 영문자, 숫자, 특수문자(!@#$%^)가 포함되어야 합니다.");
        }
        if (isPasswordSame(newPassword, user.getUserPassword())) {
            throw new BadRequestException(USER_MODIFY_PASSWORD_FAILURE);
        } else {
            user.updatePassword(passwordEncoder.encode(newPassword));
        }
    }

    /**
     * 마일리지
     */
    // 마일리지 사용(충전) 내역 조회
    public Page<MileageCommonDto.MileageDto> getMileageUsage(Long userId, int pageNo) {
        findUserById(userId);
        pageNo = pageVaildation(pageNo);
        Pageable pageable = PageRequest.of(pageNo, DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "mileageDate"));
        return mileageRepository.findByUserId(pageable, userId);
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

    // 마일리지 사용 내역 추가
    @Transactional
    public void addMileageHistory(Long userId, Integer remainMileage, Integer changeMileage) {
        Mileage mileage = Mileage.builder()
                .userId(userId)
                .remainMileage(remainMileage)
                .changeMileage(changeMileage)
                .mileageInfo(MileageContents.PURCHASE).build();
        mileageRepository.save(mileage);
    }

    // 사용자 문의 전체 조회
    public Page<InquiryResDto.UserInquiryAllResDto> getUserInquiryAllbyUserId(Long userId, int pageNo) {
        pageNo = pageVaildation(pageNo);
        Pageable pageable = PageRequest.of(pageNo, DEFAULT_PAGE_SIZE);
        findUserById(userId);
        return inquiryRepository.findUserInquiryAllByUserId(pageable, userId);
    }

    // 사용자 주문 내역 상세 조회
    public OrderResDto.OrderDetailListResDto getOrderDetailListByOrderId(Long userId, Long orderId, int pageNo) {
        pageNo = pageVaildation(pageNo);
        Pageable pageable = PageRequest.of(pageNo, DEFAULT_PAGE_SIZE);
        List<ProductCommonDto.ProductDetailListDto> productDetailList = orderDetailRepository.findOrderDetailByOrderId(orderId);
        Order order = orderRepository.findByOrderId(orderId);
        User user = userRepository.findByUserId(userId);
        return new OrderResDto.OrderDetailListResDto(order, user, productDetailList);
    }

    // 주문 / 배송 내역 조회
    public OrderResDto.OrderListResDto getOrderListResByUserId(Long userId, int pageNo) {
        pageNo = pageVaildation(pageNo);
        Pageable pageable = PageRequest.of(pageNo, DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "orderDate"));

        Page<OrderCommonDto.OrderListDto> orderList = orderRepository.findByUserId(pageable, userId)
                .map((o) -> {
                    List<ProductCommonDto.ProductDetailListDto> productDetailListDtos = orderDetailRepository.findOrderDetailByOrderId(o.getOrderId());
                    return new OrderCommonDto.OrderListDto(o, productDetailListDtos);
                });
        Page<Order> orders = orderRepository.findByUserId(pageable, userId);

        Long confPayCount = orderDetailRepository.safeCountOrderDetailByUserIdAndOrderDetailState(userId, OrderDetailState.ORDER_COMPLETE);
        Long preShipCount = orderDetailRepository.safeCountOrderDetailByUserIdAndOrderDetailState(userId, OrderDetailState.DELIVERY_READY);
        Long inTransitCount = orderDetailRepository.safeCountOrderDetailByUserIdAndOrderDetailState(userId, OrderDetailState.DELIVERY_ING);
        Long cmpltDilCount = orderDetailRepository.safeCountOrderDetailByUserIdAndOrderDetailState(userId, OrderDetailState.DELIVERY_COMPLETE);
        Long orderCancelCount = orderDetailRepository.safeCountOrderDetailByUserIdAndOrderDetailState(userId, OrderDetailState.ORDER_CANCEL);
        Long returnCount = orderDetailRepository.safeCountOrderDetailByUserIdAndOrderDetailState(userId, OrderDetailState.RETURN_REQUEST) +
                orderDetailRepository.safeCountOrderDetailByUserIdAndOrderDetailState(userId, OrderDetailState.RETURN_COMPLETE);


        OrderResDto.OrderListResDto orderListResDto = new OrderResDto.OrderListResDto();

        if (confPayCount == null) {
            orderListResDto.setConfPayCount(0L);
        } else {
            orderListResDto.setConfPayCount(confPayCount);
        }
        if (preShipCount == null) {
            orderListResDto.setPreShipCount(0L);
        } else {
            orderListResDto.setPreShipCount(preShipCount);
        }
        if (inTransitCount == null) {
            orderListResDto.setInTransitCount(0L);
        } else {
            orderListResDto.setInTransitCount(inTransitCount);
        }
        if (cmpltDilCount == null) {
            orderListResDto.setCmpltDilCount(0L);
        } else {
            orderListResDto.setCmpltDilCount(cmpltDilCount);
        }
        if (orderCancelCount == null) {
            orderListResDto.setOrderCancelCount(0L);
        } else {
            orderListResDto.setOrderCancelCount(orderCancelCount);
        }
        if (returnCount == null) {
            orderListResDto.setReturnCount(0L);
        } else {
            orderListResDto.setReturnCount(returnCount);
        }
        orderListResDto.setOrderList(orderList);

        return orderListResDto;
    }

    // 환불 요청
    @Transactional
    public OrderResDto.OrderDetailListResDto postOrderReturn(Long orderDetailId, String returnContents, int pageNo) {
        pageNo = pageVaildation(pageNo);
        Pageable pageable = PageRequest.of(pageNo, DEFAULT_PAGE_SIZE);
        // orderstate를 환불 요청으로 바꾸고 시간 현재시간으로 변경
        orderDetailRepository.updateOrderDetailUpdateDateAndOrderDetailStateByOrderDetailId(orderDetailId, OrderDetailState.RETURN_REQUEST);
        OrderDetail orderDetail = orderDetailRepository.findByOrderDetailId(orderDetailId);
        orderDetail.updateOrderDetailUpdateDate();

        // 환불 테이블에 저장
        Return returns = Return.builder()
                .orderDetailId(orderDetailId)
                .returnReason(returnContents)
                .returnState(ReturnState.RETURN_REQUEST)
                .build();
        Return saveReturn = returnRepository.save(returns);


        // Response 저장..?
        Order order = orderRepository.findByOrderDetailId(orderDetailId);
        User user = userRepository.findByUserId(order.getUserId());
        List<ProductCommonDto.ProductDetailListDto> productDetailList = orderDetailRepository.findOrderDetailByOrderId(order.getOrderId());
        return new OrderResDto.OrderDetailListResDto(order, user, productDetailList);
    }

    // 주문 취소
    @Transactional
    public OrderResDto.OrderDetailListResDto postOrderCancel(Long orderId, Long orderDetailId, Long userId, int pageNo) {
        pageNo = pageVaildation(pageNo);
        Pageable pageable = PageRequest.of(pageNo, DEFAULT_PAGE_SIZE);
        // orderstate를 주문취소로 바꾸고 시간 현재시간으로 변경
        orderDetailRepository.updateOrderDetailUpdateDateAndOrderDetailStateByOrderDetailId(orderDetailId, OrderDetailState.ORDER_CANCEL);
        OrderDetail orderDetail = orderDetailRepository.findByOrderDetailId(orderDetailId);
        orderDetail.updateOrderDetailUpdateDate();

        // 계산값 적용
        Integer orderDetailSalePrice = orderDetailRepository.orderDetailTotalSalePrice(orderDetailId);
        Integer orderDetailPrice = orderDetailRepository.orderDetailTotalPrice(orderDetailId);
        orderRepository.updateOrderTotalPrice(orderId, orderDetailSalePrice, orderDetailPrice);
        // 마일리지 적립
        userRepository.updateUserMileageByCancel(userId, orderDetailSalePrice);

        Order order = orderRepository.findByOrderDetailId(orderDetailId);
        User user = userRepository.findByUserId(userId);
        List<ProductCommonDto.ProductDetailListDto> productDetailList = orderDetailRepository.findOrderDetailByOrderId(order.getOrderId());
        return new OrderResDto.OrderDetailListResDto(order, user, productDetailList);
    }

    //사용 id로 조회
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
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

    // 페이지 예외처리
    public int pageVaildation(int page) {
        return page = page > 0 ? page - 1 : 0;
    }
}