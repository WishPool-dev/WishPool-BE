package WishPool.Be.security.filter;

import WishPool.Be.security.service.SecurityUserDto;
import WishPool.Be.util.jwt.JwtUtil;
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
