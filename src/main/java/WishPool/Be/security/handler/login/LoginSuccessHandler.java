package WishPool.Be.security.handler.login;


import WishPool.Be.security.service.CustomOAuth2User;
import WishPool.Be.user.entity.User;
import WishPool.Be.util.jwt.GeneratedToken;
import WishPool.Be.util.jwt.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = customOAuth2User.getUser();
        // 토큰 생성 및 쿠키 설정
        GeneratedToken token = jwtUtil.generateToken(user.getUserId(), String.valueOf(user.getRole()), user.getName());

        // Authorization 헤더에 Bearer 토큰 추가
        response.addHeader("Authorization", "Bearer " + token.getAccessToken());
        response.addHeader("Access-Control-Expose-Headers", "Authorization");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}