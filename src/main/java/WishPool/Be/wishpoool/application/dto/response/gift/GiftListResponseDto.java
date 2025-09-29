package WishPool.Be.wishpoool.application.dto.response.gift;

import WishPool.Be.wishpoool.domain.WishPool;

import java.util.List;
public record GiftListResponseDto(
        String celebrant,
        Long participantCount,
        List<GiftItemResponseDto> gifts
) {
    public static GiftListResponseDto from(WishPool wishPool) {
        // 1. WishPool에서 celebrant와 participantCount를 가져옴
        String celebrantName = wishPool.getCelebrant();
        long count = wishPool.getParticipants().stream()
                .filter(participant -> participant.getGiftList() != null)
                .count();
        // 2. 여러 Participant에 흩어져 있는 모든 GiftItem들을 하나의 리스트
        List<GiftItemResponseDto> allGifts = wishPool.getParticipants().stream()
                // 선물 목록이 없는 참여자는 제외
                .filter(participant -> participant.getGiftList() != null && participant.getGiftList().getGiftItems() != null)
                // flatMap을 사용해 각 참여자의 선물 리스트를 하나의 스트림으로 펼침
                .flatMap(participant -> participant.getGiftList().getGiftItems().stream()
                        // 각 GiftItem을 GiftItemResponseDto로 변환
                        .map(giftItem -> GiftItemResponseDto.from(participant, giftItem))
                )
                .toList();

        // 3. 취합한 데이터로 DTO를 생성하여 반환
        return new GiftListResponseDto(celebrantName, count, allGifts);
    }
}
