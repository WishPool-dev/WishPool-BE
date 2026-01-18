package WishPool.Be.wishpoool.application.dto.response.gift;

import WishPool.Be.wishpoool.domain.GiftItem;
import WishPool.Be.wishpoool.domain.WishPool;
import java.util.List;
import java.util.stream.Collectors;

public record SelectedGiftsDto(String celebrant, List<SelectedGiftsListDto> selectedGiftsListDto) {
    public static SelectedGiftsDto from(WishPool wishPool, List<GiftItem> giftItems){
        return new SelectedGiftsDto(
                wishPool.getCelebrant(),
                giftItems.stream().map(SelectedGiftsListDto::from).collect(Collectors.toList())
        );
    }
    public static record SelectedGiftsListDto(Long giftId, String giftName, String giftImage, String imageUrl) {
        public static SelectedGiftsListDto from(GiftItem giftItem){
            return new SelectedGiftsListDto(giftItem.getGiftItemId(), giftItem.getItemName(), giftItem.getItemUrl(), giftItem.getImageUrl());
        }
    }
}
