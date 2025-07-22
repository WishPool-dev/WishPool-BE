package WishPool.Be.security.service;


import lombok.Builder;
import lombok.Data;

@Data @Builder
public class SecurityUserDto {
    private String name;
    private String role;
    private Long userId;
}
