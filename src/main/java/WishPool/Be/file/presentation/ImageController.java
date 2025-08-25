package WishPool.Be.file.presentation;

import WishPool.Be.file.application.dto.response.ImageUploadResponse;
import WishPool.Be.file.application.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/files")
public class ImageController {
    private final FileService fileService;

    @PostMapping
    public ResponseEntity<ImageUploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ImageUploadResponse("업로드된 이미지가 없습니다."));
        }
        // 1. GCS에 저장될 파일의 키를 미리 생성합니다.
        String imageKey = UUID.randomUUID().toString();
        // 2. 비동기 메소드를 호출하여 파일 업로드를 위임합니다.
        fileService.uploadImageAsync(file, imageKey);
        ImageUploadResponse response = new ImageUploadResponse(imageKey);
        return ResponseEntity.ok().body(response);        // 3. 파일 업로드 완료를 기다리지 않고, 생성된 키를 클라이언트에게 즉시 반환합니다.
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteImage(@PathVariable String key) {
        fileService.deleteImage(key);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{key}")
    public ResponseEntity<ImageUploadResponse> getFileURL(@PathVariable String key){
        ImageUploadResponse uploadResponse = new ImageUploadResponse(fileService.getImageURL(key));
        return ResponseEntity.ok(uploadResponse);
    }
}
