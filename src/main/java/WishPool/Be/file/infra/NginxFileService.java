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
            // 2. 저장할 최종 경로 생성 (설정된 기본경로 + 파일명)
            Path targetLocation = this.uploadPath.resolve(key);
            Files.createDirectories(targetLocation.getParent());

            // 3. 파일 쓰기 (byte[] 데이터를 파일로 저장)
            // StandardOpenOption.CREATE: 없으면 생성
            // StandardOpenOption.TRUNCATE_EXISTING: 있으면 덮어쓰기
            Files.write(targetLocation, fileBytes,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            log.info("비동기 로컬 파일 저장 성공: key={}", key);

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
}
