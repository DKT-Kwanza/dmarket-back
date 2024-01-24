package com.dmarket.controller;

import com.dmarket.dto.request.AddWishReqDto;
import com.dmarket.dto.response.CMResDto;
import com.dmarket.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    // 위시리스트 추가
    @PostMapping("/{userId}/wish")
    public ResponseEntity<?> addWish(@PathVariable Long userId, @Valid @RequestBody AddWishReqDto addWishReqDto, BindingResult bindingResult){
        try {
            //request body 유효성 확인
            bindingResultErrorsCheck(bindingResult);

            // 위시리스트 등록
            Long productId = addWishReqDto.getProductId();
            userService.addWish(userId, productId);

            return new ResponseEntity<>(CMResDto.builder().code(200).msg("위시리스트 등록 성공").build(), HttpStatus.OK);
        } catch (RuntimeException e) {
            log.warn(e.getMessage(), e.getCause());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").data(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(CMResDto.builder().code(400).msg("서버 내부 오류").build(), HttpStatus.BAD_REQUEST);
        }
    }

    //validation 체크
    private void bindingResultErrorsCheck(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError fe : bindingResult.getFieldErrors()) {
                errorMap.put(fe.getField(), fe.getDefaultMessage());
            }
            throw new RuntimeException(errorMap.toString());
        }
    }
}
