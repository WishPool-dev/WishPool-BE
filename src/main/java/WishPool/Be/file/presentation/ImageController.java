package WishPool.Be.file.presentation;

import WishPool.Be.file.application.dto.response.ImageUploadResponse;
import WishPool.Be.file.application.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Tag(name = "파일/이미지 API", description = "이미지 업로드, 조회, 삭제 관련 API")
@RestController
@RequestMapping("/api/files")
public class ImageController {

    private final FileService fileService;

    public ImageController(@Qualifier("nginxFileService") FileService fileService) {
        this.fileService = fileService;
    }

    @Operation(summary = "이미지 업로드",
            description = "nginx에 이미지를 업로드하고, 즉시 이미지 키를 반환합니다. (비동기 처리)")
    @PostMapping
    public ResponseEntity<ImageUploadResponse> uploadImage(
            @Parameter(description = "업로드할 이미지 파일") @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ImageUploadResponse("업로드된 이미지가 없습니다."));
        }

        // 1. UUID 생성
        String uuid = UUID.randomUUID().toString();

        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        if (extension == null || extension.isEmpty()) {
            extension = "jpg";
        }

        String finalKey = uuid + "." + extension;

        fileService.uploadImageAsync(file.getBytes(), finalKey);
        // 5. 클라이언트에게 즉시 반환
        ImageUploadResponse response = new ImageUploadResponse(finalKey);
        return ResponseEntity.ok().body(response);        // 3. 파일 업로드 완료를 기다리지 않고, 생성된 키를 클라이언트에게 즉시 반환합니다.
    }

    @Operation(summary = "이미지 삭제", description = "GCS에 업로드된 이미지를 키(key)를 이용해 삭제합니다.")
    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteImage(
            @Parameter(description = "삭제할 이미지의 고유 키", example = "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8") @PathVariable String key) {
        fileService.deleteImage(key);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "이미지 URL 조회", description = "이미지 키(key)를 사용하여 GCS에 저장된 이미지의 Public URL을 조회합니다.")
    @GetMapping("/{key}")
    public ResponseEntity<ImageUploadResponse> getFileURL(
            @Parameter(description = "조회할 이미지의 고유 키", example = "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8") @PathVariable String key){
        ImageUploadResponse uploadResponse = new ImageUploadResponse(fileService.getImageURL(key));
        return ResponseEntity.ok(uploadResponse);
    }
}