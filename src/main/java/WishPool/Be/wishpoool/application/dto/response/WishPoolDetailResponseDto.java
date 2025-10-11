package WishPool.Be.wishpoool.application.dto.response;

import WishPool.Be.wishpoool.domain.WishPool;
import WishPool.Be.wishpoool.domain.WishPoolStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// 로그인한 사용자의 위시풀 상세 조회
public record WishPoolDetailResponseDto(
    String imageKey,
    String endDate, // LocalDate -> String으로 변경
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
                ,wishPool.getParticipantEndDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                ,wishPool.getCelebrant()
                ,joinCount
                ,wishPool.getDescription()
                ,d_day
                ,wishPool.getWishPoolStatus()
                ,wishPool.getBirthDay(),
                ownerJoined);
    }
}
