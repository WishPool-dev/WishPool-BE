package WishPool.Be.security.handler.login;

import WishPool.Be.global.exception.auth.AuthErrorResponse;
import WishPool.Be.global.exception.auth.AuthStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper;
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.warn("로그인 실패 : {}", exception.getMessage());
        // 로그인 실패니까 로그인 실패만을 명시적으로 던질 것
        AuthStatus authStatus = AuthStatus.LOGIN_FAILURE;
        AuthErrorResponse errorResponse = new AuthErrorResponse(authStatus, request.getRequestURI());
        // 3. 응답 설정 및 JSON 변환
        response.setStatus(authStatus.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}