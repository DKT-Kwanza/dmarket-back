package com.dmarket.jwt;

import com.dmarket.dto.response.CMResDto;
import com.dmarket.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.info("CustomAccessDeniedHandler");
        CMResDto<String> resDto = CMResDto.errorWithMsgRes(ErrorCode.FORBIDDEN, ErrorCode.FORBIDDEN.getMsg());
        JWTFilter.writeResponse(response, resDto);
    }
}
