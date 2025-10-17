package WishPool.Be;

import WishPool.Be.global.exception.business.BusinessException;
import WishPool.Be.global.exception.business.ErrorStatus;
import WishPool.Be.security.service.SecurityUserDto;
import WishPool.Be.user.domain.User;
import WishPool.Be.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WithdrawController {
    private final UserRepository userRepository;

    @DeleteMapping("/api/user/withdraw")
    public ResponseEntity<Void> withdraw(@AuthenticationPrincipal SecurityUserDto securityUserDto){
        User user = userRepository.findById(securityUserDto.getUserId()).orElseThrow(()-> new BusinessException(ErrorStatus.USER_NOT_FOUND));
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }
}
