package WishPool.Be.wishpoool.application.dto.response;

import WishPool.Be.wishpoool.domain.WishPoolStatus;

//홈 화면에서 최신순 3개 조회하는 것
public record WishPoolResponseDto(Long wishpoolId, WishPoolStatus wishPoolStatus, int D_day, String imageKey){}