package WishPool.Be.security.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Map;

/**
 * 인증 실패 시 예외를 처리하는 커스텀 클래스
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 1) HTTP 상태 코드
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 2) 에러 타입별 커스텀 필드
        String errorCode;
        String message = authException.getMessage();

        // instanceof = 왼쪽 객체가 오른쪽 객체 타입이거나 그 서브 타입인지 확인해주는 역할
        if (authException instanceof AuthenticationCredentialsNotFoundException) {
            errorCode = "token_missing_or_invalid";
            // message는 “유효하지 않은 jwt 토큰이거나, 토큰이 존재하지 않습니다.” 그대로
        }
        else if (authException instanceof CredentialsExpiredException) {
            errorCode = "token_expired";
            // message는 “token_expired”
        }
        else if (authException instanceof BadCredentialsException
                && "blacklisted_token".equals(message)) {
            errorCode = "token_revoked";
            message = "로그아웃되었거나 무효화된 토큰입니다.";
        }
        else {
            errorCode = "unauthorized";
        }

        // 3) JSON 바디 작성
        Map<String,Object> body = Map.of(
                "error",   errorCode,
                "message", message
        );
        objectMapper.writeValue(response.getWriter(), body);
    }
}