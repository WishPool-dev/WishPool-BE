package WishPool.Be.gcs;


import java.util.UUID;

import WishPool.Be.gcs.dto.ImageUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageUploadController {

    private final GcsService gcsService;

    @PostMapping("/upload")
    public ResponseEntity<ImageUploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ImageUploadResponse("업로드된 이미지가 없습니다."));
        }
        // 1. GCS에 저장될 파일의 키를 미리 생성합니다.
        String imageKey = UUID.randomUUID().toString();
        // 2. 비동기 메소드를 호출하여 파일 업로드를 위임합니다.
        gcsService.uploadImageAsync(file, imageKey);
        ImageUploadResponse response = new ImageUploadResponse(imageKey);
        // 3. 파일 업로드 완료를 기다리지 않고, 생성된 키를 클라이언트에게 즉시 반환합니다.
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteImage(@PathVariable String key) {
        gcsService.deleteImage(key);
        return ResponseEntity.noContent().build();
    }
}