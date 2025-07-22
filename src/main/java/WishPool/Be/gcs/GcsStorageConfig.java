package WishPool.Be.gcs;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Configuration
public class GcsStorageConfig {

    @Value("${spring.cloud.gcp.credentials.location}")
    private Resource credentialsResource;

    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;

    @Bean
    public Storage googleCloudStorage() throws IOException {
        GoogleCredentials creds = GoogleCredentials
                .fromStream(credentialsResource.getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        return StorageOptions.newBuilder()
                .setCredentials(creds)
                .setProjectId(projectId)
                .build()
                .getService();
    }
}
