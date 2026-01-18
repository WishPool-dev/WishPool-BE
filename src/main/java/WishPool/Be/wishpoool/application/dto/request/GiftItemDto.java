package WishPool.Be.wishpoool.application.dto.request;

import WishPool.Be.wishpoool.domain.GiftItem;
import jakarta.validation.constraints.NotBlank;

public record GiftItemDto(
    @NotBlank(message = "상품 URL을 입력해주세요.")
    String itemUrl,
    @NotBlank(message = "상품 이름을 입력해주세요.")
    String itemName,
    String imageUrl
    )
    {
    public static GiftItemDto from(GiftItem giftItem) {
        return new GiftItemDto(
                giftItem.getItemUrl(),
                giftItem.getItemName(),
                giftItem.getImageUrl()
        );
    }
    }
