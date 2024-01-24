package com.dmarket.controller;

import com.dmarket.constant.InquiryType;
import com.dmarket.domain.board.Inquiry;
import com.dmarket.dto.common.InquiryRequestDto;
import com.dmarket.dto.response.CMResDto;
import com.dmarket.dto.response.UserInfoResDto;
import com.dmarket.dto.response.WishlistResDto;
import com.dmarket.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/user")
    public String userP() {
        return "User Page";
    }

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

    // 문의 작성
    @PostMapping("/{userId}/board/inquiry")
    public ResponseEntity<Inquiry> createInquiry(
            @PathVariable Long userId,
            @RequestBody InquiryRequestDto inquiryRequestDto) {
        try {
            Inquiry inquiry = Inquiry.builder()
                    .userId(userId)
                    .inquiryType(inquiryRequestDto.getInquiryType())
                    .inquiryTitle(inquiryRequestDto.getInquiryTitle())
                    .inquiryContents(inquiryRequestDto.getInquiryContents())
                    .inquiryImg(inquiryRequestDto.getInquiryImg())
                    .inquiryState(false) // 기본값 false로 설정
                    .build();

            Inquiry createdInquiry = userService.createInquiry(inquiry);
            return new ResponseEntity<>(createdInquiry, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
