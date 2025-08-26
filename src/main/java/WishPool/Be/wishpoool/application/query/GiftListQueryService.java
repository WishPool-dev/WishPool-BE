package WishPool.Be.wishpoool.application.query;

import WishPool.Be.global.exception.business.BusinessException;
import WishPool.Be.global.exception.business.ErrorStatus;
import WishPool.Be.wishpoool.application.dto.response.gift.BirthdayGiftsViewResponseDto;
import WishPool.Be.wishpoool.application.dto.response.gift.GiftListResponseDto;
import WishPool.Be.wishpoool.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

// Gemini의 크랙 코드 ㄷㄷ 재사용성 매우 향상
@Service
@RequiredArgsConstructor
@Slf4j
public class GiftListQueryService {
    private final WishPoolRepository wishPoolRepository;
    private final GiftListRepository giftListRepository;

    @Transactional(readOnly = true)
    protected WishPool findWishPoolWithAllGifts(Long wishpoolId) {
        // wishpool과 참여 테이블 조인
        WishPool findWishpool = wishPoolRepository.findWithParticipantsAndGiftListById(wishpoolId)
                .orElseThrow(() -> new BusinessException(ErrorStatus.WISHPOOL_NOT_FOUND));

        // 참여 테이블에서 giftlist를 꺼내기
        List<GiftList> giftListsToFetch = findWishpool.getParticipants().stream()
                .map(Participant::getGiftList)
                .filter(Objects::nonNull)
                .toList();

        // giftlist랑 Item 패치조인
        if (!giftListsToFetch.isEmpty()) {
            giftListRepository.findWithGiftItemsByIn(giftListsToFetch);
        }
        return findWishpool;
    }


    // 기존 선물 확인 리스트들
    @Transactional(readOnly = true)
    public List<GiftListResponseDto> getAllGifts(Long wishpoolId) {
        WishPool wishPool = findWishPoolWithAllGifts(wishpoolId);
        return wishPool.getParticipants().stream()
                .map(GiftListResponseDto::from)
                .toList();
    }


    // 생일자가 링크로 보는 선물들
    @Transactional(readOnly = true)
    public BirthdayGiftsViewResponseDto getGiftsForBirthdayOwner(String chosenIdentifier) {
        WishPool found = wishPoolRepository.findByChosenIdentifier(chosenIdentifier).orElseThrow(()-> new BusinessException(ErrorStatus.WISHPOOL_NOT_FOUND));
        WishPool wishPool = findWishPoolWithAllGifts(found.getWishPoolId());

        // 선물 목록 DTO 리스트 생성
        List<GiftListResponseDto> gifts = wishPool.getParticipants().stream()
                .map(GiftListResponseDto::from)
                .toList();

        // WishPool에서 필요한 추가 정보를 꺼내서 DTO를 완성!
        return BirthdayGiftsViewResponseDto.of(
                gifts,
                wishPool.getCelebrant(), // 예시 필드
                wishPool.getBirthDay(),
                wishPool.getCelebrantPickEndDate()
        );
    }
}

