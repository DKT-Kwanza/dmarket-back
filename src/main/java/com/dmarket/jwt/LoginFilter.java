package com.dmarket.jwt;

import com.dmarket.domain.user.RefreshToken;
import com.dmarket.dto.common.UserCommonDto;
import com.dmarket.dto.response.CMResDto;
import com.dmarket.dto.response.UserResDto;
import com.dmarket.repository.user.RefreshTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;


// SpringSecurity 로그인 경로 설정 문제로 @RequiredArgsConstructor 설정 X
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;


    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;

        // login 경로 변경
        setFilterProcessesUrl("/api/users/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 로그인 요청 API 에서 password 값을 추출
        String password = obtainPassword(request);

        // 로그인 요청 API 에서 useremaul 값을 추출
        String useremail = request.getParameter("email");

        // 유저 정보 검증을 위해 이메일, 패스워드 값 전달
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(useremail, password, null);

        return authenticationManager.authenticate(authToken);
    }

    // 객체로 변환 필요
    private ObjectMapper objectMapper = new ObjectMapper();

    // 요청받은 정보가 DB에 있는 사용자인 경우
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        UserResDto.CustomUserDetails customUserDetails = (UserResDto.CustomUserDetails) authentication.getPrincipal();

        String email = customUserDetails.getEmail();
        Long userId = customUserDetails.getUserId();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        // AccessToken 만료 시간 6분 -> AccessToken 만료 시간 30분
        String accesstoken = jwtUtil.createAccessJwt(userId, email, role);

        // RefreshToken 만료 시간 240시간
        String refreshtoken = jwtUtil.createRefreshJwt(userId);

        refreshTokenRepository.save(new RefreshToken(refreshtoken, accesstoken, email));

        UserCommonDto.TokenResponseDto tokenResponseDto = new UserCommonDto.TokenResponseDto(accesstoken, refreshtoken, userId, role);

        CMResDto<UserCommonDto.TokenResponseDto> cmRespDto = CMResDto.<UserCommonDto.TokenResponseDto>builder()
                .code(200)
                .msg("Success")
                .data(tokenResponseDto)
                .build();

        // HttpServletRequest 에 body에 정보를 담기.
        writeResponse(response, cmRespDto);
    }

    // 요청받은 정보가 DB에 없는 사용자인 경우
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        CMResDto<Void> cmRespDto = CMResDto.<Void>builder()
                .code(HttpServletResponse.SC_UNAUTHORIZED) // 401 Unauthorized
                .msg("아이디 또는 비밀번호가 틀렸습니다.")
                .build();

        writeResponse(response, cmRespDto);
        response.setStatus(401);
    }


    private void writeResponse(HttpServletResponse response, CMResDto<?> cmRespDto) {
        try {
            // cmRespDto 객체로 변환해서 타입 반환.
            ObjectMapper objectMapper = new ObjectMapper();

            // cmRespDto 내부에 LocalDatetime 형식 변환 설정.
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"));

            // response body에 담기.
            String jsonResponse = objectMapper.writeValueAsString(cmRespDto);

            // response 타입지정.
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // response 반환.
            response.getWriter().write(jsonResponse);

        } catch (IOException e) {
            // 에러 핸들링
            e.printStackTrace();
        }
    }

}
