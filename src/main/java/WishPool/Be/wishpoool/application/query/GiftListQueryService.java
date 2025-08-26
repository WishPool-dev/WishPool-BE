package WishPool.Be.wishpoool.application.query;

import WishPool.Be.global.exception.business.BusinessException;
import WishPool.Be.global.exception.business.ErrorStatus;
import WishPool.Be.wishpoool.application.dto.response.gift.GiftListResponseDto;
import WishPool.Be.wishpoool.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class GiftListQueryService {
    private final WishPoolRepository wishPoolRepository;
    private final GiftListRepository giftListRepository;

    // 특정 wishpool에 걸려있는 선물 리스트 조회
    @Transactional(readOnly = true)
    public List<GiftListResponseDto> getAllGifts(Long wishpoolId) {
        // wishpool과 참여 테이블 조인
        WishPool findWishpool = wishPoolRepository.findWithParticipantsAndGiftListById(wishpoolId)
                .orElseThrow(() -> new BusinessException(ErrorStatus.WISHPOOL_NOT_FOUND));
        // 참여 테이블에서 giftlist를 꺼내기
        List<GiftList> giftListsToFetch = findWishpool.getParticipants().stream()
                .map(Participant::getGiftList) // 참여자 목록을 선물 리스트 목록으로 변환
                .filter(Objects::nonNull)      // 선물 리스트가 없는(null) 경우는 제외
                .toList();
        // giftlist랑 Item 패치조인
        if (!giftListsToFetch.isEmpty()) {
            giftListRepository.findWithGiftItemsByIn(giftListsToFetch);
        }
        return findWishpool.getParticipants().stream()
                .map(GiftListResponseDto::from)
                .toList();
    }
}

