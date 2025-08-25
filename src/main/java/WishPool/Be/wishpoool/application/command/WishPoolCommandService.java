package WishPool.Be.wishpoool.application.command;

import WishPool.Be.global.exception.business.BusinessException;
import WishPool.Be.global.exception.business.ErrorStatus;
import WishPool.Be.security.service.SecurityUserDto;
import WishPool.Be.user.domain.User;
import WishPool.Be.user.domain.UserRepository;
import WishPool.Be.util.IdentifierGenerator;
import WishPool.Be.wishpoool.application.dto.request.CreateWishPoolRequestDto;
import WishPool.Be.wishpoool.application.dto.response.CreatedWishPoolResponseDto;
import WishPool.Be.wishpoool.domain.WishPool;
import WishPool.Be.wishpoool.domain.WishPoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishPoolCommandService {
    private final WishPoolRepository wishPoolRepository;
    private final UserRepository userRepository;

    // 위시풀 생성하기
    @Transactional
    public CreatedWishPoolResponseDto creatWishPool(CreateWishPoolRequestDto dto, SecurityUserDto securityUserDto){
        User owner = userRepository.findById(securityUserDto.getUserId())
                .orElseThrow(()-> new BusinessException(ErrorStatus.USER_NOT_FOUND));
        String url = IdentifierGenerator.generateShareIdentifier();
        WishPool wishPoolToSave = WishPool.createWishPool(dto, owner, url);
        // DTO에 엔티티를 받는 생성자를 만들기
        return new CreatedWishPoolResponseDto(wishPoolRepository.save(wishPoolToSave));
    }
}
