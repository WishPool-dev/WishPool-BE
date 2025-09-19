package WishPool.Be.contact;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public record ContactRequestDto(
        @NotNull(message = "별점을 설정해주세요.")
        int rating,

        @NotBlank(message = "평가하신 내용을 추가해주세요.")
        String content) {
}
