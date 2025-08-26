package WishPool.Be.wishpoool.application.dto.response.gift;

import java.time.LocalDate;
import java.util.List;

public record BirthdayGiftsViewResponseDto(
        List<GiftListResponseDto> gifts,
        String  celebrant,
        LocalDate birthDay,
        LocalDate endPickDate
) {
    public static BirthdayGiftsViewResponseDto of(List<GiftListResponseDto> gifts, String celebrant, LocalDate birthDay, LocalDate endPickDate) {
        return new BirthdayGiftsViewResponseDto(gifts, celebrant, birthDay, endPickDate);
    }
}