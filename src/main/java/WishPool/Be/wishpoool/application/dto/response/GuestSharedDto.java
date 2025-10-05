package WishPool.Be.wishpoool.application.dto.response;

import WishPool.Be.wishpoool.domain.WishPool;

public record GuestSharedDto(String shareIdentifier, Long wishpoolId) {
    public static GuestSharedDto from(WishPool wishPool){
        return new GuestSharedDto(wishPool.getShareIdentifier(), wishPool.getWishPoolId());
    }

    public static GuestSharedDto completedLinkFrom(WishPool wishPool){
        return new GuestSharedDto(wishPool.getCompleteIdentifier(), wishPool.getWishPoolId());
    }
}
