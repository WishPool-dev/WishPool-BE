package WishPool.Be.security.exception.login;


import WishPool.Be.global.exception.auth.AuthStatus;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;
@Getter
public class OAuthProcessingException extends AuthenticationException {
    private final AuthStatus authStatus;

    public OAuthProcessingException(AuthStatus authStatus) {
        super(authStatus.getMessage());
        this.authStatus = authStatus;
    }
}
