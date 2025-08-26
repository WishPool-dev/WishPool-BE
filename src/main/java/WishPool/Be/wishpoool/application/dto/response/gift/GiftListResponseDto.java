package WishPool.Be.wishpoool.application.dto.response.gift;

import WishPool.Be.wishpoool.application.dto.request.GiftItemDto;
import WishPool.Be.wishpoool.domain.Participant;

import java.util.Collections;
import java.util.List;

public record GiftListResponseDto(String guest, List<GiftItemDto> gifts) {
    public static GiftListResponseDto from(Participant participant) {
        // 참여자는 선물 리스트가 없을 수도 있으므로(null), 안전하게 처리
        List<GiftItemDto> items = participant.getGiftList() == null
                ? Collections.emptyList() // 선물 리스트가 없으면 빈 리스트를,
                : participant.getGiftList().getGiftItems().stream() // 있다면 그 안의 선물들을 DTO로 번역
                .map(GiftItemDto::from) // 위에서 만든 GiftItemDto.from() 사용
                .toList();
        return new GiftListResponseDto(participant.getGuestName(), items);
    }
}
