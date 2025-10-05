package WishPool.Be.wishpoool.application.dto.response;

import WishPool.Be.wishpoool.domain.WishPool;

public record GuestSharedDto(String shareIdentifier, Long wishpoolId) {
    public static GuestSharedDto of(WishPool wishPool){
        return new GuestSharedDto(wishPool.getShareIdentifier(), wishPool.getWishPoolId());
    }
}
