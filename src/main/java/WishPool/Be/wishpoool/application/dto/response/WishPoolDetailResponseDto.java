package WishPool.Be.wishpoool.application.dto.response;

import WishPool.Be.wishpoool.domain.WishPool;
import WishPool.Be.wishpoool.domain.WishPoolStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

// 로그인한 사용자의 위시풀 상세 조회
public record WishPoolDetailResponseDto(
    String imageKey,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd", timezone = "Asia/Seoul")
    LocalDate endDate,
    String celebrant,
    Long joinCount,
    String description,
    //d_day = 마감까지 남은 기간, endDate와 비슷해서 이렇게 구분함
    int d_day,
    WishPoolStatus status,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd", timezone = "Asia/Seoul")
    LocalDate birthDay,
    boolean ownerJoined
){
    public static WishPoolDetailResponseDto from(WishPool wishPool, Long joinCount, int d_day, boolean ownerJoined){
        return new WishPoolDetailResponseDto(
                wishPool.getImageKey()
                ,wishPool.getParticipantEndDate()
                ,wishPool.getCelebrant()
                ,joinCount
                ,wishPool.getDescription()
                ,d_day
                ,wishPool.getWishPoolStatus()
                ,wishPool.getBirthDay(),
                ownerJoined);
    }
}
