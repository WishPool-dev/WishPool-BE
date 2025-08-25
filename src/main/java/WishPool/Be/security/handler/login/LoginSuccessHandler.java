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
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = customOAuth2User.getUser();
        // 토큰 생성
        GeneratedToken token = jwtUtil.generateToken(user.getUserId(), String.valueOf(user.getRole()), user.getName());
        // 2) JSON 응답 바디 세팅
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 예시로 accessToken만, 필요하면 refreshToken 등 추가
        Map<String, Object> responseBody = Map.of(
                "accessToken",  token.getAccessToken(),
                "code", 200,
                "message", "로그인/회원가입에 성공했습니다."
        );
        objectMapper.writeValue(response.getWriter(), responseBody);
    }
}