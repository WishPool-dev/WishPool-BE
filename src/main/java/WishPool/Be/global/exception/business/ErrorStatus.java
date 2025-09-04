package WishPool.Be.global.exception.business;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@AllArgsConstructor
@Getter
public enum ErrorStatus {
    NOT_FOUND(HttpStatus.NOT_FOUND, "못찾겠다!!"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, ""),
    WISHPOOL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 위시풀을 찾을 수 없습니다."),
    WISHPOOL_NOT_IN_PENDING_STATE(HttpStatus.NOT_ACCEPTABLE, "해당 위시풀은 아직 참여기간이 남았습니다."),
    INVALID_PICK_END_DATE(HttpStatus.NOT_ACCEPTABLE, "해당 위시풀의 사용자 참여일이 남아있습니다."),
    WISHPOOL_NOT_OPEN(HttpStatus.BAD_REQUEST, "해당 위시풀을 수정할 수 없는 상태입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다. 로그인 후 이용해주세요.");
    private final HttpStatus httpStatus;
    private final String message;
}
