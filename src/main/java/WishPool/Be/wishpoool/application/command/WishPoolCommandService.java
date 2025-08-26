package WishPool.Be.wishpoool.application.command;

import WishPool.Be.wishpoool.application.dto.request.CreateGiftListRequestDto;
import WishPool.Be.global.exception.business.BusinessException;
import WishPool.Be.global.exception.business.ErrorStatus;
import WishPool.Be.security.service.SecurityUserDto;
import WishPool.Be.user.domain.User;
import WishPool.Be.user.domain.UserRepository;
import WishPool.Be.util.IdentifierGenerator;
import WishPool.Be.wishpoool.application.dto.request.CreateWishPoolRequestDto;
import WishPool.Be.wishpoool.application.dto.response.CelebrantUrlResponseDto;
import WishPool.Be.wishpoool.application.dto.response.CreatedWishPoolResponseDto;
import WishPool.Be.wishpoool.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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

        if (wishPool.getWishPoolStatus() != WishPoolStatus.OPEN) {
            throw new BusinessException(ErrorStatus.WISHPOOL_NOT_OPEN); // 예시 에러
        }

        // 3. 사용자 검증
        User owner = userRepository.findById(securityUserDto.getUserId())
                .orElseThrow(()-> new BusinessException(ErrorStatus.USER_NOT_FOUND));
        // 4. 참여자 조회
        Participant participant = participantRepository.findWishPoolOwner(wishPool.getWishPoolId(), ParticipantRole.OWNER);
        // 5. 선물 리스트 추가
        participant.addGiftListByOwner(dto);
        // 6. 변경 사항 저장 (이 부분은 명시적으로 호출할 필요 없을 수 있습니다)
        // wishPoolRepository.save(wishPool);
        return wishPool.getWishPoolId();
    }

    // 참여 마감일이 지난 후 상태 변경, 선물 선택일 날짜 지정 (open -> pending -> waiting) 만약 사용자가 마감을 즉시 바꾸고 싶다면, 오늘 날짜로 바꾸거나 어제 날짜로 바꾸어야할듯
    @Transactional(readOnly = false)
    public CelebrantUrlResponseDto getChosenURL(Long wishpoolId, LocalDate pickDate, Long userId){
        // 이렇게 사용자와 위시풀 ID 둘 다 아는게 제일 좋음, 깜빡했음
        Participant participant = participantRepository.findParticipantByUserAndWishPool(userId, wishpoolId);
        WishPool selected = participant.getWishPool();
        // 상태 바꾸고 URL 전달
        String chosenUrl = selected.startGiftSelection(pickDate);
        return new CelebrantUrlResponseDto(wishpoolId, chosenUrl);
    }

    // 스케줄러로 마감되는 위시풀들 상태 수정하기
    @Transactional(readOnly = false)
    public void changeOpenWishPoolsToPending(LocalDate expiredDate) {
        // 참여 마감일이 어제 날짜이고, 상태가 OPEN인 위시풀들을 모두 찾음
        List<WishPool> expiredPools = wishPoolRepository.findAllByWishPoolStatusAndParticipantEndDate(
                WishPoolStatus.OPEN,
                expiredDate
        );
        // 상태를 PENDING으로 변경
        expiredPools.forEach(WishPool::changeStatusToPending);
        // TODO: 각 WishPool의 주최자에게 메시지 보내기 ?
    }

}
