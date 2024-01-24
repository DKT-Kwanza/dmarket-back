package com.dmarket.jwt;

import com.dmarket.constant.Role;
import com.dmarket.domain.user.RefreshToken;
import com.dmarket.domain.user.User;
import com.dmarket.dto.response.CMResDto;
import com.dmarket.dto.response.CustomUserDetails;
import com.dmarket.dto.response.TokenResponseDto;
import com.dmarket.repository.user.RefreshTokenRepository;
import com.dmarket.repository.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class JWTFilter extends OncePerRequestFilter {

    @Value("${application.security.jwt.expireT}")
    private Long jwtExpiration;

    @Value("${application.security.jwt.ReFreshexpireT}")
    private Long RefreshjwtExpiration;

    private final JWTUtil jwtUtil;

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;


    public JWTFilter(JWTUtil jwtUtil, UserRepository userRepository , RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //request에서 Authorization 헤더를 찾음
        String authorization = request.getHeader("Authorization");
        System.out.println("authorization = " + authorization);

        //Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {

            System.out.println("token null");
            filterChain.doFilter(request, response);

            //조건이 해당되면 메소드 종료 (필수)
            return;
        }
        // 헤더가 있기 때문에 헤더를 추출
        String token = authorization.split(" ")[1];
        System.out.println("token = " + token);

        // 헤더가 만료되었는 확인
        try {
            jwtUtil.isExpired(token);
        } catch (ExpiredJwtException e) {

            System.out.println("token expired");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            CMResDto<Void> cmRespDto = CMResDto.<Void>builder()
                    .code(HttpServletResponse.SC_UNAUTHORIZED) // 401 Unauthorized
                    .msg("토큰이 만료되었습니다.")
                    .build();

            writeResponse(response, cmRespDto);

            //filterChain.doFilter(request, response);
            return;
        }


        String type = jwtUtil.getType(token);
        System.out.println("1.type = " + type);

        String email = jwtUtil.getEmail(token);
        System.out.println("1.email = " + email);
        String role = jwtUtil.getRole(token);

        if(Objects.equals(type, "RTK")){
            if(refreshTokenRepository.existsByRefreshToken(token)){
                refreshTokenRepository.deleteByUserEmail(email);
                String newAccessToken = jwtUtil.createAccessJwt(email,role,jwtExpiration);
                String newRefreshtoken = jwtUtil.createRefreshJwt(email, role, RefreshjwtExpiration);
                saveRefreshTokenToDatabase(email,newRefreshtoken);
                System.out.println("Hello");

                TokenResponseDto tokenResponseDto = new TokenResponseDto();
                tokenResponseDto.setAccesstoken(newAccessToken);
                tokenResponseDto.setRefreshtoken(newRefreshtoken);

                CMResDto<TokenResponseDto> cmRespDto = CMResDto.<TokenResponseDto>builder()
                        .code(200)
                        .msg("새로운 토큰 발급 Success")
                        .data(tokenResponseDto)
                        .build();

                writeResponse(response, cmRespDto);

                return;

            }else{
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                CMResDto<Void> cmRespDto = CMResDto.<Void>builder()
                        .code(HttpServletResponse.SC_UNAUTHORIZED) // 401 Unauthorized
                        .msg("Refresh토큰이 없습니다. 다시 로그인 해주세요")
                        .build();

                writeResponse(response, cmRespDto);

                //filterChain.doFilter(request, response);
                return;
            }
        }
        System.out.println("HelloAA");

        User userEntity = new User(email,777,"temppassword","username", LocalDate.now(),"77-89",11," ","");
//        userEntity.setUserName("username");
//        userEntity.setUserPassword("temppassword");
//        userEntity.setUserEmail(email);
//        userEntity.setUserRole(Role.valueOf(role));
//        userEntity.setUserMileage(1000);
//        userEntity.setUserAddressDetail("AddressDetail");
//        userEntity.setUserAddress("UserAddress");
//        userEntity.setUserPostalCode(7);
//        userEntity.setUserDktNum(777);
//        userEntity.setUserPhoneNum("77-89");


        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }

    // JSON 응답을 생성하는 메소드
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
    private void saveRefreshTokenToDatabase(String userEmail, String refreshToken) {
        RefreshToken refreshTokendata = new RefreshToken(userEmail,refreshToken);
//        refreshTokendata.setUserEmail(userEmail);
//        refreshTokendata.setRefreshToken(refreshToken);

        refreshTokenRepository.save(refreshTokendata);
    }
}
