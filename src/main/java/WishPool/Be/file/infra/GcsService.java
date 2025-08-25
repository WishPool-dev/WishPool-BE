package WishPool.Be.file.infra;

import WishPool.Be.file.application.service.FileService;
import WishPool.Be.file.exception.GcsUploadException;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class GcsService implements FileService {

    private final Storage storage;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    @Override
    @Async // 이 메소드를 별도의 스레드에서 비동기로 실행
    public void uploadImageAsync(MultipartFile file, String key) {
        try {
            String contentType = file.getContentType();
            storage.create(
                    BlobInfo.newBuilder(bucketName, key)
                            .setContentType(contentType)
                            .build(),
                    file.getInputStream()
            );
            // 성공/실패에 대한 로그를 남기는 것이 좋음
        } catch (IOException e) {
            log.error("비동기 GCS 업로드 실패: key={}", key, e);
            throw new GcsUploadException("비동기 GCS 업로드에 실패했습니다.", e);
        }
    }

    // true면 삭제, false면 실패
    @Override
    public boolean deleteImage(String key) {
        return storage.delete(bucketName, key);
    }

    @Override
    public String getImageURL(String key) {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, key).build();
        // 15분 동안 유효한 V4 서명의 Presigned URL을 생성
        return storage.signUrl(
                blobInfo,
                15, // URL 유효 시간
                TimeUnit.MINUTES, // 시간 단위
                Storage.SignUrlOption.withV4Signature()
        ).toString();
    }
}