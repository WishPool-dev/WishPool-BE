package WishPool.Be.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * http 예외 발생 시 발생 API 경로 및 시간을 표현하는 클래스
 */
@Getter
public class MetaData {
    private final String path;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime timestamp;
    public MetaData(String path) {
        this.path = path;
        this.timestamp = LocalDateTime.now(); // 객체 생성 시점의 현재 시간
    }
}
