package WishPool.Be.wishpoool.application.dto.response.gift;

import WishPool.Be.wishpoool.domain.GiftItem;
import WishPool.Be.wishpoool.domain.Participant;

public record GiftItemResponseDto(String guest, String itemUrl, String itemName, Long giftId) {
    public static GiftItemResponseDto from(Participant participant, GiftItem giftItem) {
        return new GiftItemResponseDto(
                participant.getGuestName(),
                giftItem.getItemUrl(),
                giftItem.getItemName(),
                giftItem.getGiftItemId()
        );
    }
}
