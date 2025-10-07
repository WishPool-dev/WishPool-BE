package WishPool.Be.wishpoool.application.dto.response;

import WishPool.Be.wishpoool.domain.WishPool;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

// URL 타고 조회하는 용도
public record WishPoolGuestInfoResponseDto(
        String owner,
        String celebrant,
        LocalDate endDate,
        String description,
        Long wishpoolId,
        @DateTimeFormat(pattern = "yyyy/mm/dd")
        LocalDate birthDay,
        String imageKey
        ) {
    public WishPoolGuestInfoResponseDto(WishPool wishPool){
        this(wishPool.getOwnerName()
                ,wishPool.getCelebrant()
                , wishPool.getParticipantEndDate()
                , wishPool.getDescription()
                , wishPool.getWishPoolId()
                , wishPool.getBirthDay()
                ,wishPool.getImageKey());
    }
}
