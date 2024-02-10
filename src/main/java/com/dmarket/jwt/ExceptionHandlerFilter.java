package com.dmarket.jwt;

import com.dmarket.dto.response.CMResDto;
import com.dmarket.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("ExceptionHandlerFilter");
        try {
            log.debug("doFilter");
            String authHeader = jwtUtil.getAuthHeader(request);
            String token = jwtUtil.getToken(authHeader);
            filterChain.doFilter(request, response);

        } catch (NullPointerException e) {
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            //만료된 토큰
            String msg = ErrorCode.EXPIRED_TOKEN.getMsg();
            log.warn(msg, e.getCause());
            CMResDto<String> resDto = CMResDto.errorWithMsgRes(ErrorCode.EXPIRED_TOKEN, msg);
            JWTFilter.writeResponse(response, resDto);

        } catch (JwtException | IllegalArgumentException e) {
            //유효하지 않은 토큰
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
