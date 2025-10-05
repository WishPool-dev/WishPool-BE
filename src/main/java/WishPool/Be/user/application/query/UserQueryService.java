package WishPool.Be.user.application.query;

import WishPool.Be.global.exception.business.BusinessException;
import WishPool.Be.global.exception.business.ErrorStatus;
import WishPool.Be.user.application.dto.response.UserProfileDto;
import WishPool.Be.user.domain.User;
import WishPool.Be.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserQueryService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(Long userId){
        User user = userRepository.findById(userId).orElseThrow(()-> new BusinessException(ErrorStatus.USER_NOT_FOUND));
        return UserProfileDto.of(user);
    }

}
