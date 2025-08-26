package WishPool.Be.wishpoool.application.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record StartSelectionRequestDto(
        @NotNull(message = "선물 선택 마감일을 입력해주세요.")
        @FutureOrPresent(message = "마감 날짜는 오늘 날짜 이후여야 합니다.")
        LocalDate pickDate
) {}