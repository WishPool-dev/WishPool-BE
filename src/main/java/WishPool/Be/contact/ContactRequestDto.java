package WishPool.Be.contact;

import jakarta.validation.constraints.*;

public record ContactRequestDto(
        @NotNull(message = "별점을 설정해주세요.")
        @Min(value = 1, message = "별점은 최소 1점 이상이어야 합니다.")
        @Max(value = 5, message = "별점은 최대 5점까지 가능합니다.")
        int rating,

        @NotBlank(message = "평가하신 내용을 추가해주세요.")
        @Size(max = 200, message = "내용은 최대 200자까지 입력 가능합니다.")
        String content) {
}
