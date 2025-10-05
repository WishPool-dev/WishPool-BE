package WishPool.Be.user.presentation;

import WishPool.Be.security.service.SecurityUserDto;
import WishPool.Be.user.application.dto.response.UserProfileDto;
import WishPool.Be.user.application.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserQueryService userQueryService;
    @PostMapping("/logout")
    public ResponseEntity<String> logout(){
        return ResponseEntity.ok().body("로그아웃 되었습니다.");
    }

    @GetMapping("/api/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(@AuthenticationPrincipal SecurityUserDto securityUserDto){
        return ResponseEntity.ok().body(userQueryService.getUserProfile(securityUserDto.getUserId()));
    }
}
