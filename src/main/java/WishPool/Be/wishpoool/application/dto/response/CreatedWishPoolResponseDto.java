package WishPool.Be.wishpoool.application.dto.response;

import WishPool.Be.wishpoool.domain.WishPool;

public record CreatedWishPoolResponseDto(Long wishpoolId, String shareIdentifier) {
    public CreatedWishPoolResponseDto(WishPool wishPool) {
        this(wishPool.getWishPoolId(), wishPool.getShareIdentifier());
    }
}