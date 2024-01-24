package com.dmarket.controller;

import com.dmarket.dto.response.*;
import com.dmarket.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    // 위시리스트 조회
    @GetMapping("/{userId}/wish")
    public ResponseEntity<?> getWishlistByUserId(@PathVariable(name = "userId") Long userId) {
        try {
            WishlistResDto wishlist = userService.getWishlistByUserId(userId);
            log.info("데이터 조회 완료");
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("위시리스트 조회 완료").data(wishlist).build(), HttpStatus.OK);
        }
        catch (IllegalArgumentException e) {
            // 잘못된 요청에 대한 예외 처리
            log.warn("유효하지 않은 요청 메시지: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").build(), HttpStatus.BAD_REQUEST);
        }
        // catch (AuthenticationException e) {
        // // 인증 오류에 대한 예외 처리
        // log.warn("유효하지 않은 인증" + e.getMessage());
        // return new ResponseEntity<>(CMResDto.builder()
        //         .code(401).msg("유효하지 않은 인증").build(), HttpStatus.UNAUTHORIZED);
        // }
        catch (Exception e) {
            // 기타 예외에 대한 예외 처리
            log.error("서버 내부 오류: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg("서버 내부 오류").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 장바구니 상품 개수 조회
    @GetMapping("{userId}/cart-count")
    public ResponseEntity<?> getCartCount(@PathVariable(name = "userId") Long userId){
        try{
            CartCountResDto cartCount = userService.getCartCount(userId);
            log.info("데이터 조회 완료");
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("장바구니 상품 개수 조회 완료").data(cartCount).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // 잘못된 요청에 대한 예외 처리
            log.warn("유효하지 않은 요청 메시지:" + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").build(), HttpStatus.BAD_REQUEST);
//        } catch (AuthenticationException e) {
//            // 인증 오류에 대한 예외 처리
//            log.warn("유효하지 않은 인증" + e.getMessage());
//            return new ResponseEntity<>(CMResDto.builder()
//                    .code(401).msg("유효하지 않은 인증").build(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            // 기타 예외에 대한 예외 처리
            log.error("서버 내부 오류: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg("서버 내부 오류").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 마이페이지 서브헤더 사용자 정보 및 마일리지 조회
    @GetMapping("/{userId}/mypage/mileage")
    public ResponseEntity<?> getSubHeader(@PathVariable(name = "userId") Long userId){
        try{
            UserHeaderInfoResDto subHeader = userService.getSubHeader(userId);
            log.info("데이터 조회 완료");
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("장바구니 상품 개수 조회 완료").data(subHeader).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // 잘못된 요청에 대한 예외 처리
            log.warn("유효하지 않은 요청 메시지:" + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").build(), HttpStatus.BAD_REQUEST);
//        } catch (AuthenticationException e) {
//            // 인증 오류에 대한 예외 처리
//            log.warn("유효하지 않은 인증" + e.getMessage());
//            return new ResponseEntity<>(CMResDto.builder()
//                    .code(401).msg("유효하지 않은 인증").build(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            // 기타 예외에 대한 예외 처리
            log.error("서버 내부 오류: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg("서버 내부 오류").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 위시리스트 삭제
    @DeleteMapping("/{userId}/wish/{wishlistIds}")
    public ResponseEntity<?> deleteWishlistId(@PathVariable(name = "userId") Long userId,@PathVariable(name = "wishlistIds") List<Long> wishlistIds){
        try {
            for (Long wishlistId : wishlistIds) {
                userService.deleteWishlistById(wishlistId);
            }
            log.info("데이터 삭제 완료");
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("위시리스트 삭제 완료").build(), HttpStatus.OK);
        }
        catch (IllegalArgumentException e) {
            // 잘못된 요청에 대한 예외 처리
            log.warn("유효하지 않은 요청 메시지: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").build(), HttpStatus.BAD_REQUEST);
        }
        // catch {
        //     // 인증 오류에 대한 예외 처리
        //     log.warn("유효하지 않은 인증" + e.getMessage());
        //     return new ResponseEntity<>(CMResDto.builder()
        //             .code(401).msg("유효하지 않은 인증").build(), HttpStatus.UNAUTHORIZED);
        // }
        catch (Exception e) {
            // 기타 예외에 대한 예외 처리
            log.error("서버 내부 오류: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg("서버 내부 오류").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 사용자 정보 조회
    @GetMapping("/{userId}/mypage/myinfo")
    public ResponseEntity<?> getUserInfoByUserId(@PathVariable(name = "userId") Long userId) {
        try {
            UserInfoResDto userInfo = userService.getUserInfoByUserId(userId);
            log.info("데이터 조회 완료");
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("사용자 정보 조회 완료").data(userInfo).build(), HttpStatus.OK);
        }
        catch (IllegalArgumentException e) {
            // 잘못된 요청에 대한 예외 처리
            log.warn("유효하지 않은 요청 메시지: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").build(), HttpStatus.BAD_REQUEST);
        }
        //     catch (AuthenticationException e) {
        //     // 인증 오류에 대한 예외 처리
        //     log.warn("유효하지 않은 인증" + e.getMessage());
        //     return new ResponseEntity<>(CMResDto.builder()
        //             .code(401).msg("유효하지 않은 인증").build(), HttpStatus.UNAUTHORIZED);
        //    }
        catch (Exception e) {
            // 기타 예외에 대한 예외 처리
            log.error("서버 내부 오류: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500).msg("서버 내부 오류").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
