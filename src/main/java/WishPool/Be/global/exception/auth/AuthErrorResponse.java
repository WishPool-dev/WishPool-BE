package WishPool.Be.global.exception.auth;

import WishPool.Be.common.MetaData;
import lombok.Getter;

@Getter
public class AuthErrorResponse {
    private final int code;
    // http 상태 코드, 메시지
    private final String errorMessage;
    // URI, timestamp
    private final MetaData metaData;
    public AuthErrorResponse(AuthStatus authStatus, String path){
        this.code = authStatus.getHttpStatus().value();
        this.metaData = new MetaData(path);
        this.errorMessage = authStatus.getMessage();
    }
}
