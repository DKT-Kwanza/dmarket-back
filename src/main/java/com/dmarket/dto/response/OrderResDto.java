package com.dmarket.dto.response;
import java.time.LocalDateTime;
import java.util.List;

import com.dmarket.domain.order.*;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Data
@Getter
@NoArgsConstructor
public class OrderResDto<T> {
    private LocalDateTime orderDate;
    private Long orderId;
    private List<T> orderDetailList; // 제네릭 리스트 사용

    // 제네릭 생성자
    public OrderResDto(Order order, List<T> details){
        this.orderDate = order.getOrderDate();
        this.orderId = order.getOrderId();
        this.orderDetailList = details;
    }
    
    // Getter, Setter 생략
}

