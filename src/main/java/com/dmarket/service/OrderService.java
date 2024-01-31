package com.dmarket.service;

import com.dmarket.constant.OrderDetailState;
import com.dmarket.domain.order.Order;
import com.dmarket.domain.order.OrderDetail;
import com.dmarket.domain.product.Product;
import com.dmarket.domain.product.ProductOption;
import com.dmarket.domain.user.User;
import com.dmarket.dto.request.OrderReqDto;
import com.dmarket.dto.request.ProductReqDto;
import com.dmarket.dto.response.OrderResDto;
import com.dmarket.dto.response.ProductResDto;
import com.dmarket.jwt.JWTUtil;
import com.dmarket.repository.order.OrderDetailRepository;
import com.dmarket.repository.order.OrderRepository;
import com.dmarket.repository.product.ProductImgsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    //조회가 아닌 메서드들은 꼭 @Transactional 넣어주세요 (CUD, 입력/수정/삭제)

    private final UserService userService;
    private final ProductService productService;

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductImgsRepository productImgsRepository;

    private final JWTUtil jwtUtil;

    // 결제할 상품 조회
    public ProductResDto.ProductToOrderRespDto getProductToOrder(ProductReqDto.ProductToOrderReqDto dto) {

        Integer totalPrice = 0;
        Integer totalPay = 0;
        ProductResDto.ProductToOrderRespDto respDto = new ProductResDto.ProductToOrderRespDto();
        ArrayList<ProductResDto.ProductToOrderRespDto.ProductToOrder> productList = new ArrayList<>();
        List<ProductReqDto.ProductToOrderReqDto.ProductToOrder> products = dto.getProductList();

        for (ProductReqDto.ProductToOrderReqDto.ProductToOrder productToOrder : products) {

            Long productId = productToOrder.getProductId();
            Integer productCount = productToOrder.getProductCount();
            Long optionId = productToOrder.getOptionId();
            Product product = productService.findProductById(productId);

            ProductResDto.ProductToOrderRespDto.ProductToOrder productDetail = new ProductResDto.ProductToOrderRespDto.ProductToOrder();
            productDetail.setProductId(productId);
            productDetail.setProductBrand(product.getProductBrand());
            productDetail.setProductName(product.getProductName());
            productDetail.setOptionId(optionId);
            productDetail.setProductOption(productService.findOptionByOptionId(optionId).getOptionValue());
            productDetail.setProductCount(productCount);
            productDetail.setProductImg(productImgsRepository.findFirstByProductId(productId).getImgAddress());
            productDetail.setProductTotalPrice(product.getProductPrice() * productCount);
            productDetail.setProductTotalSalePrice(product.getProductSalePrice() * productCount);

            totalPrice += product.getProductPrice() * productCount;
            totalPay += product.getProductSalePrice() * productCount;
            productList.add(productDetail);
        }

        User user = userService.findUserById(dto.getUserId());
        respDto.setUserName(user.getUserName());
        respDto.setUserPhoneNum(user.getUserPhoneNum());
        respDto.setUserEmail(user.getUserEmail());
        respDto.setUserPostalCode(user.getUserPostalCode());
        respDto.setUserAddress(user.getUserAddress());
        respDto.setUserDetailAddress(user.getUserAddressDetail());
        respDto.setTotalPrice(totalPrice);
        respDto.setTotalPay(totalPay);
        respDto.setProductList(productList);

        return respDto;
    }

    // 주문
    @Transactional
    public OrderResDto<String> payment(OrderReqDto.OrderPaymentReqDto dto) {

        //DTO 데이터 추출
        Long userId = dto.getUserId();
        Integer orderTotalPrice = dto.getOrderTotalPrice();
        Integer orderTotalPay = dto.getOrderTotalPay();
        OrderReqDto.OrderPaymentReqDto.OrderDetail[] productList = dto.getOrderDetailList();

        //본인이 맞는지 확인
        /**
         * bizLogic
         */

        //잔액과 비교하여 결제 가능한지 확인
        User user = userService.findUserById(userId);
        Integer userMileage = user.getUserMileage();
        if (orderTotalPay > userMileage) {
            throw new IllegalStateException("마일리지 잔액이 부족합니다.");
        }

        //결제 가능하면 결제 (마일리지 차감)
        user.updateMileage(-orderTotalPay);
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

        //마일리지 사용 내역 추가
        userService.addMileageHistory(userId, userMileage - orderTotalPay, -orderTotalPay);

        //Order 생성
        Long orderId = createOrder(userId, orderTotalPrice, orderTotalPay, now);

        //OrderDetail 생성
        for (OrderReqDto.OrderPaymentReqDto.OrderDetail orderDetail : productList) {
            //주문 상세 데이터 추출
            Long productId = orderDetail.getProductId();
            Long optionId = orderDetail.getOptionId();
            Integer orderDetailCount = orderDetail.getOrderDetailCount();
            Integer orderDetailPrice = orderDetail.getOrderDetailPrice();
            Integer orderDetailSalePrice = orderDetail.getOrderDetailSalePrice();

            createOrderDetail(orderId, optionId, productId, orderDetailCount, orderDetailPrice, orderDetailSalePrice);
        }

        //반환
        OrderResDto<String> resDto = new OrderResDto<>();
        resDto.setOrderId(orderId);
        resDto.setOrderDate(now);
        resDto.setOrderDetailList(null);
        return resDto;
    }

    @Transactional
    public Long createOrder(Long userId, Integer orderTotalPrice, Integer orderTotalPay, LocalDateTime now) {
        Order order = Order.builder()
                .userId(userId)
                .orderTotalPrice(orderTotalPrice)
                .orderTotalPay(orderTotalPay)
                .orderDate(now)
                .build();
        return orderRepository.save(order).getOrderId();
    }

    @Transactional
    public Long createOrderDetail(Long orderId, Long optionId, Long productId, Integer orderDetailCount, Integer orderDetailPrice, Integer orderDetailSalePrice) {
        OrderDetail orderDetail = OrderDetail.builder()
                .orderId(orderId)
                .optionId(optionId)
                .productId(productId)
                .orderDetailState(OrderDetailState.ORDER_COMPLETE)
                .orderDetailCount(orderDetailCount)
                .orderDetailPrice(orderDetailPrice)
                .orderDetailSalePrice(orderDetailSalePrice).build();
        ProductOption option = productService.findOptionByOptionId(optionId);
        option.removeOptionQuantity(orderDetailCount);
        return orderDetailRepository.save(orderDetail).getOrderDetailId();
    }
}
