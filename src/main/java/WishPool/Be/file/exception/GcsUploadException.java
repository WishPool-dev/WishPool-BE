package WishPool.Be.file.exception;

// GCS 업로드 중 발생하는 예외임을 명확히 하기 위한 클래스
public class GcsUploadException extends RuntimeException {
    public GcsUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}