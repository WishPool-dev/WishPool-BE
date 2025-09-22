package WishPool.Be.security;

import WishPool.Be.security.filter.CustomAuthenticationEntryPoint;
import WishPool.Be.security.filter.OAuth2JwtAuthFilter;
import WishPool.Be.security.handler.login.LoginFailureHandler;
import WishPool.Be.security.handler.login.LoginSuccessHandler;
import WishPool.Be.security.service.UserServiceOauth2;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserServiceOauth2 userService;
    private final LoginFailureHandler loginFailureHandler;
    private final LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2JwtAuthFilter oAuth2JwtAuthFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // CSRF 비활성화 (REST API 모드)
                .csrf(csrf -> csrf.disable())
                // 폼 로그인/기본 로그인 HTTP 302 리다이렉트 비활성화
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                // 세션 사용 안함 (Stateless)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 예외 처리: 인증 실패 시 CustomAuthenticationEntryPoint 사용
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                // 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // OAuth2 로그인용 엔드포인트, 정적 리소스, 홈 등은 모두 허용
                        .requestMatchers("/actuator/health/**").permitAll()
                        .requestMatchers(
                                "/login",
                                "/login/**",
                                "/oauth2/authorization/**",
                                "/login/oauth2/code/**",
                                "/css/**", "/js/**",
                                "/api/images/upload",
                                "/login-error",
                                "/home",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/contact/**"
                        ).permitAll()
                        // /api/** 는 인증 필요
                        .requestMatchers("/api/**").authenticated()
                        // 나머지는 모두 허용 (필요에 따라 변경)
                        .anyRequest().permitAll()
                )
                // OAuth2 로그인 설정 (인증 성공/실패 핸들러 등록)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .authorizationEndpoint(ep -> ep.baseUri("/oauth2/authorization"))
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                        .userInfoEndpoint(ui -> ui.userService(userService))
                );

        // JWT 토큰 검증 필터를 AuthorizationFilter 앞에 추가, UsernamePasswordAuthenticationFilter는 폼로그인을 사용하지 않으면서 아예 쓸모 없어짐
        http.addFilterBefore(oAuth2JwtAuthFilter, AuthorizationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173","https://wishpool.vercel.app/"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}