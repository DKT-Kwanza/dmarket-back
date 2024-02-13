package com.dmarket.config;

import com.dmarket.jwt.*;
import com.dmarket.repository.user.RefreshTokenRepository;
import com.dmarket.repository.user.UserRepository;
import com.dmarket.service.LogoutService;
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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
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

    // 계층 권한 설정
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("""
                ROLE_GM > ROLE_PM
                ROLE_GM > ROLE_SM
                ROLE_PM > ROLE_USER
                ROLE_SM > ROLE_USER
                """
        );

        return hierarchy;
    }

    //패스워드 인코딩
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // cors
        http
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfiguration()));

        // csrf disable
        http
                .csrf(AbstractHttpConfigurer::disable);

        // From 로그인 방식 disable
        http
                .formLogin(AbstractHttpConfigurer::disable);

        // http basic 인증 방식 disable
        http
                .httpBasic(AbstractHttpConfigurer::disable);

        // Session 설정
        http
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 로그아웃 접근 경로 설정, 접근 시 동작할 서비스(logoutService) 지정
        http
                .logout((logout) -> logout.logoutUrl("/api/users/logout")
                        .addLogoutHandler(logoutService)
                        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                );

        // 접근 권한 설정
        // 계층 권한으로 페이지 접근 제한
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/api/users/login", "/api/users/email/**", "/api/users/join").permitAll()
//                        .requestMatchers("/api/admin/**").hasAnyRole("GM", "SM", "PM")
                        .anyRequest().permitAll());
//                        .anyRequest().authenticated());


        // Error Handling
        http
                .exceptionHandling((eh) -> eh
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint(jwtUtil))
                        .accessDeniedHandler(new CustomAccessDeniedHandler()));

        // UsernamePasswordAuthenticationFilter 자리에 LoginFilter 삽입 (실제로 override 되지는 않음)
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshTokenRepository), UsernamePasswordAuthenticationFilter.class);

        // LoginFilter 앞에 JWTFilter 삽입
        http.addFilterBefore(new JWTFilter(jwtUtil, userRepository, refreshTokenRepository), LoginFilter.class);

        // JWTFilter 앞에 ExceptionHandlerFilter 삽입
//        http.addFilterBefore(new ExceptionHandlerFilter(jwtUtil), JWTFilter.class);

        return http.build();
    }

    private CorsConfigurationSource corsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
        configuration.setAllowedOriginPatterns(corsPath);  //2024-02-02 수정
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost*", "https://localhost*", "http://172.16.210*", "https://172.16.210*", "http://172.30.3.55*", "https://172.30.3.55*", "http://dmarketmall*", "https://dmarketmall*", "http://61.109.214.63*", "https://61.109.214.63*"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setExposedHeaders(Collections.singletonList("Authorization"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
