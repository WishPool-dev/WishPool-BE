package WishPool.Be.wishpoool.application.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.time.LocalDate;

@Builder
public record CreateWishPoolRequestDto(
        @NotBlank(message = "생일자 이름을 입력해주세요.")
        String celebrant,

        @NotNull(message = "생일 날짜를 입력해주세요.")
        LocalDate birthDay,

        @NotBlank(message = "위시풀에 대한 설명을 입력해주세요.")
        String description,

        String imageKey,

        @NotNull(message = "마감일을 등록해주세요.")
        @FutureOrPresent(message = "마감 날짜는 오늘 날짜 이후여야 합니다.")
        LocalDate endDate
) {}