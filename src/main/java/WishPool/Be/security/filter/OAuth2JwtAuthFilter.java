package WishPool.Be.security.filter;

import WishPool.Be.security.service.SecurityUserDto;
import WishPool.Be.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // 로그인 시작/콜백, 헬스체크 등 필터를 적용하지 않을 경로 패턴
        return path.startsWith("/login")
                || path.startsWith("/oauth2/authorization/")
                || path.startsWith("/login/oauth2/code/")
                || path.startsWith("/actuator/health")
                || path.startsWith("/actuator/health/")
                || path.startsWith("/api/images/upload")
                // 필요하다면 swagger, static 리소스 등도 추가
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/api/test")
                || path.startsWith("/contact");

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String atc = request.getHeader("Authorization");
        // [수정] 1. 토큰이 존재하고 "Bearer "로 시작하는 경우에만 인증 절차를 시도합니다.
        if (StringUtils.hasText(atc) && atc.startsWith("Bearer ")) {
            String accessToken = atc.substring(7);
            try {
                // [수정] 2. 토큰이 유효한 경우(verifyAccessToken 통과)에만 SecurityContext에 인증 정보를 등록합니다.
                if (jwtUtil.verifyAccessToken(accessToken)) {
                    log.info("여기 걸림");
                    String userId = jwtUtil.getId(accessToken);
                    String role = jwtUtil.getRole(accessToken);
                    String name = jwtUtil.getName(accessToken);

                    // SecurityContext에 등록할 User 객체를 만들어줍니다.
                    SecurityUserDto userDto = SecurityUserDto.builder()
                            .userId(Long.valueOf(userId))
                            .role(role)
                            .name(name)
                            .build();

                    // SecurityContext에 인증 객체를 등록해줍니다.
                    Authentication auth = getAuthentication(userDto);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
                // 'verifyAccessToken'이 false를 반환하면 (유효하지 않음)
                // 이 블록을 그냥 건너뛰고 익명 사용자로 진행됩니다.

            } catch (Exception e) {
                // [수정] 3. 토큰 검증 중 예외(예: 만료, 서명 오류)가 발생해도 요청을 막지 않고 로그만 남깁니다.
                //    요청은 익명 사용자로 계속 진행됩니다.
                log.warn("JWT 토큰 검증 오류 (요청은 익명으로 계속): {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
    public Authentication getAuthentication(SecurityUserDto member) {
        return new UsernamePasswordAuthenticationToken(member, "",
                List.of(new SimpleGrantedAuthority(member.getRole())));
    }
}
