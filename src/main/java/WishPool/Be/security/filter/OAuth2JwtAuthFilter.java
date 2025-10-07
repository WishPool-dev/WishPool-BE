package WishPool.Be.security.filter;

import WishPool.Be.security.service.SecurityUserDto;
import WishPool.Be.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
                || path.startsWith("/contact")
                || path.startsWith("/wishpools")
                || path.startsWith("/wishpools/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String atc = request.getHeader("Authorization");
        if (!StringUtils.hasText(atc) || !atc.startsWith("Bearer ")) {
            throw  new AuthenticationCredentialsNotFoundException("유효하지 않은 jwt 토큰이거나, 토큰이 존재하지 않습니다.");
        }
        String accessToken = atc.substring(7);

        // AccessToken의 값이 있고, 유효한 경우에 진행한다.
        if (jwtUtil.verifyAccessToken(accessToken)) {
            String userId = jwtUtil.getId(accessToken);
            String role = jwtUtil.getRole(accessToken);
            String name = jwtUtil.getName(accessToken);
            // SecurityContext에 등록할 User 객체를 만들어준다.
            SecurityUserDto userDto = SecurityUserDto.builder()
                    .userId(Long.valueOf(userId))
                    .role(role)
                    .name(name)
                    .build();
            // SecurityContext에 인증 객체를 등록해준다.
            Authentication auth = getAuthentication(userDto);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
    public Authentication getAuthentication(SecurityUserDto member) {
        return new UsernamePasswordAuthenticationToken(member, "",
                List.of(new SimpleGrantedAuthority(member.getRole())));
    }
}
