package com.dmarket.jwt;

import com.dmarket.dto.response.CMResDto;
import com.dmarket.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final JWTUtil jwtUtil;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String authHeader = jwtUtil.getAuthHeader(request);
        try {
            log.info("commence");
            log.info("authException");
            log.debug(authException.getMessage(), authException.getCause());

            String token = jwtUtil.getToken(authHeader);
            response.setHeader("token", token);
            CMResDto<String> resDto = CMResDto.errorWithMsgRes(ErrorCode.INVALID_TOKEN, authException.getLocalizedMessage());
            JWTFilter.writeResponse(response, resDto);

        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage(), e.getCause());
            CMResDto<String> resDto = CMResDto.errorWithMsgRes(ErrorCode.INVALID_TOKEN, e.getMessage());
            JWTFilter.writeResponse(response, resDto);

        } catch (IndexOutOfBoundsException e) {
            //토큰으로 Bearer만 오면 터지는 예외
            String msg = ErrorCode.INVALID_TOKEN.getMsg();
            log.warn(msg, e.getCause());
            CMResDto<String> resDto = CMResDto.errorWithMsgRes(ErrorCode.INVALID_TOKEN, msg);
            JWTFilter.writeResponse(response, resDto);

        } catch (RuntimeException e) {
            log.warn(e.getMessage(), e.getCause());
            CMResDto<String> resDto = CMResDto.errorWithMsgRes(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            JWTFilter.writeResponse(response, resDto);
        }
    }
}
