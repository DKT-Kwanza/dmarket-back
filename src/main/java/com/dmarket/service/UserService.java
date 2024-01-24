package com.dmarket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final CartRepository cartRepository;
    private final QnaRepository qnaRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
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

    
}
