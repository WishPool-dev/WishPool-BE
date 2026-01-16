package WishPool.Be.file.application.service;


public interface FileService {
    public void uploadImageAsync(byte[] fileBytes, String key);
    public boolean deleteImage(String key);
    public String getImageURL(String key);
}
