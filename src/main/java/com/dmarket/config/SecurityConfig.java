package com.dmarket.config;

import com.dmarket.jwt.JWTFilter;
import com.dmarket.jwt.JWTUtil;
import com.dmarket.jwt.LoginFilter;
import com.dmarket.repository.user.RefreshTokenRepository;
import com.dmarket.repository.user.UserRepository;
import com.dmarket.service.LogoutService;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;
    private final LogoutService logoutService;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil, UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, LogoutService logoutService) {

        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.logoutService = logoutService;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    // 계층 권한 설정
    @Bean
    public RoleHierarchy roleHierarchy() {

        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();

        hierarchy.setHierarchy("ROLE_GM > ROLE_PM\n" +
                "ROLE_GM > ROLE_SM\n" +
                "ROLE_PM > ROLE_USER\n" +
                "ROLE_SM > ROLE_USER"
        );

        return hierarchy;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //csrf disable
        http
                .csrf((auth) -> auth.disable());

        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());


        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/", "/join").permitAll()
                        .requestMatchers("/api/admin/GM").hasRole("GM")
                        .requestMatchers("/api/admin/PM").hasRole("PM")
                        .requestMatchers("/api/admin/SM").hasRole("SM")
                        .requestMatchers("/user").hasRole("USER")
                        .anyRequest().authenticated()
                );


        http
                .addFilterBefore(new JWTFilter(jwtUtil, userRepository, refreshTokenRepository), LoginFilter.class);
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil,refreshTokenRepository), UsernamePasswordAuthenticationFilter.class);
        http
                .logout((logout) -> logout.logoutUrl("/api/users/logout")
                        .addLogoutHandler(logoutService)
                        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                );
        //세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
