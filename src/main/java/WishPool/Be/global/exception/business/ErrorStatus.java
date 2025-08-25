package WishPool.Be.global.exception.business;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@AllArgsConstructor
@Getter
public enum ErrorStatus {
    NOT_FOUND(HttpStatus.NOT_FOUND, "못찾겠다!!"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, ""),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다. 로그인 후 이용해주세요.");
    private final HttpStatus
            httpStatus;
    private final String message;
}
