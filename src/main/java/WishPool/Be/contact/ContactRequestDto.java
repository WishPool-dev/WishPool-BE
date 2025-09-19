package WishPool.Be.contact;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public record ContactRequestDto(
        @NotNull(message = "별점을 설정해주세요.")
        @Min(value = 1, message = "별점은 최소 1점 이상이어야 합니다.")
        @Max(value = 5, message = "별점은 최대 5점까지 가능합니다.")
        int rating,

        @NotBlank(message = "평가하신 내용을 추가해주세요.")
        String content) {
}
