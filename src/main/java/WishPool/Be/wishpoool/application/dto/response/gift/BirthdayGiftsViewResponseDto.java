package WishPool.Be.wishpoool.application.dto.response.gift;

import java.time.LocalDate;
import java.util.List;

public record BirthdayGiftsViewResponseDto(
        Long wishpoolId,
        List<GiftListResponseDto> gifts,
        String  celebrant,
        LocalDate birthDay,
        LocalDate endPickDate
) {
    public static BirthdayGiftsViewResponseDto of(Long wishpoolId, List<GiftListResponseDto> gifts, String celebrant, LocalDate birthDay, LocalDate endPickDate) {
        return new BirthdayGiftsViewResponseDto(wishpoolId, gifts, celebrant, birthDay, endPickDate);
    }
}