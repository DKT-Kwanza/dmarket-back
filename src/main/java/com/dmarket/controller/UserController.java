package com.dmarket.controller;

import com.dmarket.dto.request.JoinReqDto;
import com.dmarket.dto.response.CMResDto;
import com.dmarket.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody JoinReqDto dto, BindingResult bindingResult) {

        try {
            //유효성 확인
            bindingResultErrorsCheck(bindingResult);
            userService.verifyJoin(dto);

            Long userId = userService.join(dto);
            return new ResponseEntity<>(CMResDto.builder()
                    .code(200).msg("회원가입 성공").data("userId=" + userId).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage(), e.getCause());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("회원가입 실패").data(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            log.warn(e.getMessage(), e.getCause());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("회원가입 실패").data(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
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
