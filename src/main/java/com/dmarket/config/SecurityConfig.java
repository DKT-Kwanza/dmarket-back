package com.dmarket.config;

import com.dmarket.jwt.JWTFilter;
import com.dmarket.jwt.JWTUtil;
import com.dmarket.jwt.LoginFilter;
import com.dmarket.repository.user.RefreshTokenRepository;
import com.dmarket.repository.user.UserRepository;
import com.dmarket.service.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final AuthenticationConfiguration authenticationConfiguration;
        private final JWTUtil jwtUtil;
        private final RefreshTokenRepository refreshTokenRepository;
        private final UserRepository userRepository;
        private final LogoutService logoutService;

        @Value("${spring.cors.path}")
        private List<String> corsPath;

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

                return configuration.getAuthenticationManager();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                // CORS 설정
                http
                                .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
                                        CorsConfiguration configuration = new CorsConfiguration();
                                        configuration.setAllowedOrigins(
                                                        Collections.singletonList("http://localhost:3000"));
                                        configuration.setAllowedOriginPatterns(corsPath); // 2024-02-02 수정
                                        configuration.setAllowedMethods(Collections.singletonList("*"));
                                        configuration.setAllowCredentials(true);
                                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                                        configuration.setMaxAge(3600L);
                                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));
                                        return configuration;
                                }));

                // CSRF 비활성화
                http.csrf(auth -> auth.disable());

                // Form 로그인 비활성화
                http.formLogin(auth -> auth.disable());

                // HTTP Basic 인증 비활성화
                http.httpBasic(auth -> auth.disable());

                // 접근 권한 설정
                http.authorizeHttpRequests(auth -> auth
                                .requestMatchers("/", "/api/users/login", "/api/users/email/**", "/api/users/join")
                                .permitAll()
                                .anyRequest().permitAll());

                // JWT 필터 추가
                http.addFilterBefore(new JWTFilter(jwtUtil, userRepository, refreshTokenRepository), LoginFilter.class);

                // 로그인 필터 추가
                http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil,
                                refreshTokenRepository), UsernamePasswordAuthenticationFilter.class);

                // 로그아웃 설정
                http.logout(logout -> logout.logoutUrl("/api/users/logout")
                                .addLogoutHandler(logoutService)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder
                                                .clearContext()));

                // 세션 정책 설정
                http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                return http.build();
        }
}
