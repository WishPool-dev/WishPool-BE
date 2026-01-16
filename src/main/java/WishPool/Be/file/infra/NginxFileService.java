package WishPool.Be.file.infra;

import WishPool.Be.file.application.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Service
@Slf4j
@RequiredArgsConstructor
public class NginxFileService implements FileService {
    private final Path uploadPath;

    @Value("${file.image-url}")
    private String accessUrl;

    @Override
    @Async
    public void uploadImageAsync(byte[] fileBytes, String key, String contentType) {
        log.info("비동기 로컬 파일 저장 시작: key={}", key);
        try {
            String extension = getExtension(contentType);

            // 파일명 뒤에 확장자를 붙여서 저장 경로 생성
            String filename = key + extension;
            Path targetLocation = this.uploadPath.resolve(filename);

            Files.createDirectories(targetLocation.getParent());

            // 3. 파일 쓰기
            Files.write(targetLocation, fileBytes,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            log.info("비동기 로컬 파일 저장 성공: 파일명={}", filename);

        } catch (IOException e) {
            log.error("비동기 로컬 파일 저장 실패: key={}", key, e);
            throw new RuntimeException("비동기 파일 저장에 실패했습니다.", e);
        }
    }
    @Override
    public boolean deleteImage(String key) {
        try {
            //var/www/wishpool
            Path targetLocation = this.uploadPath.resolve(key);
            return Files.deleteIfExists(targetLocation);
        } catch (IOException e) {
            log.error("로컬 파일 삭제 실패: key={}", key, e);
            return false;
        }
    }

    @Override
    public String getImageURL(String key) {
        return accessUrl + key;
    }

    private String getExtension(String contentType) {
        if (contentType == null) return "";

        switch (contentType) {
            case "image/jpeg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                return ".gif";
            // 필요하면 더 추가하세요 (svg, webp 등)
            default:
                return ""; // 알 수 없는 타입이면 확장자 없이 저장
        }
    }
}
