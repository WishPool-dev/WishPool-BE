package WishPool.Be.wishpoool.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateGiftListRequestDto(
        @NotBlank(message = "이름을 입력해주세요.")
        String guestName,
        @NotNull(message = "wishPoolId는 Null을 허용하지 않습니다.")
        Long wishpoolId,
        @NotEmpty(message = "상품 명과 이름을 입력해주세요.")
        List<GiftItemDto> giftItemDto
) {
}
