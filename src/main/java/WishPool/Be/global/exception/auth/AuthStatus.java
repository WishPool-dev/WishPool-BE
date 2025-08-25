package WishPool.Be.global.exception.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@AllArgsConstructor @Getter
public enum AuthStatus {
    NOT_FOUND(HttpStatus.NOT_FOUND, "못찾겠다!!"),
    LOGIN_FAILURE(HttpStatus.BAD_REQUEST, "로그인 실패!!");
    private final HttpStatus httpStatus;
    private final String message;
}
