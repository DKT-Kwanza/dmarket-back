package com.dmarket.controller;

import com.dmarket.dto.request.ProductReqDto;
import com.dmarket.dto.response.CMResDto;
import com.dmarket.dto.response.ProductResDto;
import com.dmarket.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/products")
    public ResponseEntity<?> productToOrder(@RequestBody ProductReqDto.ProductToOrderReqDto dto) {
        try {
            ProductResDto.ProductToOrderRespDto respDto = orderService.getProductToOrder(dto);
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("success").data(respDto).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("Failed").data(e.getMessage()).build(), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg("Failed").data(e.getMessage()).build(), HttpStatus.OK);
        }
    }
}
