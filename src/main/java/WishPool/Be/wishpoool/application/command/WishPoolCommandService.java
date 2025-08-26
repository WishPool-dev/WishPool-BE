package WishPool.Be.wishpoool.application.command;

import WishPool.Be.wishpoool.application.dto.request.CreateGiftListRequestDto;
import WishPool.Be.global.exception.business.BusinessException;
import WishPool.Be.global.exception.business.ErrorStatus;
import WishPool.Be.security.service.SecurityUserDto;
import WishPool.Be.user.domain.User;
import WishPool.Be.user.domain.UserRepository;
import WishPool.Be.util.IdentifierGenerator;
import WishPool.Be.wishpoool.application.dto.request.CreateWishPoolRequestDto;
import WishPool.Be.wishpoool.application.dto.response.CreatedWishPoolResponseDto;
import WishPool.Be.wishpoool.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishPoolCommandService {
    private final WishPoolRepository wishPoolRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;

    // 위시풀 생성하기
    @Transactional
    public CreatedWishPoolResponseDto creatWishPool(CreateWishPoolRequestDto dto, SecurityUserDto securityUserDto){
        User owner = userRepository.findById(securityUserDto.getUserId())
                .orElseThrow(()-> new BusinessException(ErrorStatus.USER_NOT_FOUND));
        String inviteUrl = IdentifierGenerator.generateShareIdentifier();
        String chosenUrl = IdentifierGenerator.generateShareIdentifier();
        WishPool wishPoolToSave = WishPool.createWishPool(dto, owner, inviteUrl, chosenUrl);
        // DTO에 엔티티를 받는 생성자를 만들기
        return new CreatedWishPoolResponseDto(wishPoolRepository.save(wishPoolToSave));
    }

    // 오직 게스트만
    @Transactional(readOnly = false)
    public Long createGiftListForGuest(CreateGiftListRequestDto dto) {
        // 1. 위시풀 찾기
        WishPool wishPool = wishPoolRepository.findById(dto.wishpoolId())
                .orElseThrow(() -> new BusinessException(ErrorStatus.WISHPOOL_NOT_FOUND));
        // 2. 참여자 생성
        Participant guestParticipant = wishPool.addGuest(dto);
        // 3. 위시풀 저장
        wishPoolRepository.save(wishPool);
        return wishPool.getWishPoolId();
    }

    // 나도 참여하기 시 선물 리스트 생성
    @Transactional(readOnly = false)
    public Long createGiftListForOwner(SecurityUserDto securityUserDto, CreateGiftListRequestDto dto) {
        // 1. 위시풀 찾기
        WishPool wishPool = wishPoolRepository.findById(dto.wishpoolId())
                .orElseThrow(() -> new BusinessException(ErrorStatus.WISHPOOL_NOT_FOUND));
        // 2. 사용자 검증
        User owner = userRepository.findById(securityUserDto.getUserId())
                .orElseThrow(()-> new BusinessException(ErrorStatus.USER_NOT_FOUND));
        // 3. 참여자 조회
        Participant participant = participantRepository.findWishPoolOwner(wishPool.getWishPoolId(), ParticipantRole.OWNER);
        // 4. 선물 리스트 추가
        participant.addGiftListByOwner(dto);
        // 5. 변경 사항 저장
        wishPoolRepository.save(wishPool);
        return wishPool.getWishPoolId();
    }

    // 참여 마감일이 지난 후 상태 변경, 선물 선택일 날짜 지정(기본은 참가자 참여 + 7일)
}
