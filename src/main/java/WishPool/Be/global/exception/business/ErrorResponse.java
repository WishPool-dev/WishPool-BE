package WishPool.Be.global.exception.business;

import WishPool.Be.common.MetaData;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse{
    // http 상태 코드
    private final int code;
    // http 상태 코드, 메시지
    private final String errorMessage;
    // URI, timestamp
    private final MetaData metaData;
    public ErrorResponse(ErrorStatus errorStatus, String path){
        this.code = errorStatus.getHttpStatus().value();
        this.metaData = new MetaData(path);
        this.errorMessage = errorStatus.getMessage();
    }

    // 유효성 검사 전용 Response
    public ErrorResponse(HttpStatus httpStatus, String inputMessage,String path){
        this.code = httpStatus.value();
        this.metaData = new MetaData(path);
        this.errorMessage = inputMessage;
    }
}
