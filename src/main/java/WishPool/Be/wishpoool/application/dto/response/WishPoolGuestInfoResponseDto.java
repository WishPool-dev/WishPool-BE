package WishPool.Be.wishpoool.application.dto.response;

import WishPool.Be.wishpoool.domain.WishPool;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

// URL 타고 조회하는 용도
public record WishPoolGuestInfoResponseDto(
        String owner,
        String celebrant,
        LocalDate endDate,
        String description,
        Long wishpoolId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd", timezone = "Asia/Seoul")
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
