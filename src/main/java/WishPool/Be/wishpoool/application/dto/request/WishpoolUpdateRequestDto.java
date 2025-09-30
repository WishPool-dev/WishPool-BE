package WishPool.Be.wishpoool.application.dto.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record WishpoolUpdateRequestDto(
        String celebrant,

        // yyyy-MM-dd 형식으로 입력을 받도록 설정합니다.
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate birthDay,

        // description은 최대 200자까지 가능하도록 설정합니다.
        @Size(max = 200, message = "설명은 200자를 넘을 수 없습니다.")
        String description,

        // yyyy-MM-dd 형식으로 입력을 받고, 오늘 또는 미래의 날짜인지 검증합니다.
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        @FutureOrPresent(message = "마감일은 오늘 이후의 날짜여야 합니다.")
        LocalDate endDate
) {
}
