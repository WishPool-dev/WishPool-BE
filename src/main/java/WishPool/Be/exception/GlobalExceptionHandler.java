package WishPool.Be.exception;

import WishPool.Be.gcs.exception.GcsUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// RestController를 통해 발생하는 예외를 전역으로 처리
@RestControllerAdvice
public class GlobalExceptionHandler {
    // GcsUploadException 타입의 예외가 처리
    @ExceptionHandler(GcsUploadException.class)
    public ResponseEntity<String> handleGcsUploadException(GcsUploadException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500 에러
                .body(ex.getMessage());
    }

}
