package WishPool.Be.file.application.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    public void uploadImageAsync(byte[] fileBytes, String key, String contentType);
    public boolean deleteImage(String key);
    public String getImageURL(String key);
}
