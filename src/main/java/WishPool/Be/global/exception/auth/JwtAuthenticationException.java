package WishPool.Be.global.exception.auth;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;
// JWT 처리하면 됨, 잡히는건 commence에서 잡혀서 처리됨. 그때 AuthErrorResponse로 던지면 됨
@Getter
public class JwtAuthenticationException extends AuthenticationException {
    private final AuthStatus authStatus;
    public JwtAuthenticationException(AuthStatus authStatus) {
        super(authStatus.getMessage());
        this.authStatus = authStatus;
    }
}
