package WishPool.Be.global.exception;

import WishPool.Be.global.exception.business.BusinessException;
import WishPool.Be.global.exception.business.ErrorResponse;
import WishPool.Be.global.exception.business.ErrorStatus;
import WishPool.Be.file.exception.GcsUploadException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // GCS 예외 처리
    @ExceptionHandler(GcsUploadException.class)
    public ResponseEntity<String> handleGcsUploadException(GcsUploadException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500 에러
                .body(ex.getMessage());
    }

    // 유효성 검사 처리용 Response
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {

        String inputMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse("입력 값이 유효하지 않습니다.");
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        ErrorResponse responseBody = new ErrorResponse(HttpStatus.BAD_REQUEST, inputMessage, path);

        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    // 비즈니스 로직 처리용 Response
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, WebRequest webRequest) {
        ErrorStatus errorStatus = ex.getErrorStatus();
        //URI 경로 포함
        String path = ((ServletWebRequest)webRequest).getRequest().getRequestURI();
        ErrorResponse responseBody = new ErrorResponse(errorStatus, path);

        return new ResponseEntity<>(responseBody, errorStatus.getHttpStatus());
    }
}
