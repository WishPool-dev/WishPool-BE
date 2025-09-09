package WishPool.Be.wishpoool.application.dto.response.gift;

import WishPool.Be.wishpoool.application.dto.request.GiftItemDto;
import WishPool.Be.wishpoool.domain.GiftItem;

public record GiftItemResponseDto(String itemUrl, String itemName, Long giftId) {
    public static GiftItemResponseDto from(GiftItem giftItem) {
        return new GiftItemResponseDto(
                giftItem.getItemUrl(),
                giftItem.getItemName(),
                giftItem.getGiftItemId()
        );
    }
}
