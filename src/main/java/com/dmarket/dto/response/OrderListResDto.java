package com.dmarket.dto.response;

import com.dmarket.dto.common.OrderListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderListResDto {
    private Long confPayCount;           //결제 완료 상태 개수
    private Long preShipCount;           // 배송 준비 중 상태 개수
    private Long inTransitCount;         // 배송 중 상태 개수
    private Long cmpltDilCount;          // 배송 완료 상태 개수
    private Long orderCancelCount;       // 주문 취소 상태 개수
    private Long returnCount;            // 반품 상태 개수 RETURN_REQUEST + RETURN_COMPLETE
    private List<OrderListDto> orderList;   // 주문 리스트
}
