package WishPool.Be.wishpoool.application.dto.response.gift;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public record BirthdayGiftsViewResponseDto(
        Long wishpoolId,
        List<GiftItemResponseDto> gifts,
        String  celebrant,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd", timezone = "Asia/Seoul")
        LocalDate birthDay,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd", timezone = "Asia/Seoul")
        LocalDate endPickDate,
        String imageKey
) {
    public static BirthdayGiftsViewResponseDto of(Long wishpoolId, List<GiftItemResponseDto> gifts, String celebrant, LocalDate birthDay, LocalDate endPickDate, String imageKey) {
        return new BirthdayGiftsViewResponseDto(wishpoolId, gifts, celebrant, birthDay, endPickDate, imageKey);
    }
}