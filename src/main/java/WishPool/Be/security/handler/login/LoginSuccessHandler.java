package WishPool.Be.security.handler.login;


import WishPool.Be.security.service.CustomOAuth2User;
import WishPool.Be.user.domain.User;
import WishPool.Be.jwt.GeneratedToken;
import WishPool.Be.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    // ObjectMapper는 이제 필요 없으므로 삭제해도 됩니다.

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = customOAuth2User.getUser();

        // 1. 토큰 생성 (기존과 동일)
        GeneratedToken token = jwtUtil.generateToken(user.getUserId(), String.valueOf(user.getRole()), user.getName());
        String accessToken = token.getAccessToken();

        // 2. 리다이렉트할 URL 생성
        String targetUrl = UriComponentsBuilder.fromUriString("https://wishpool.store/auth/callback")
                .queryParam("accessToken", accessToken)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        // 3. 리다이렉트 실행
        // SimpleUrlAuthenticationSuccessHandler에서 제공하는 리다이렉트 로직을 사용합니다.
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}