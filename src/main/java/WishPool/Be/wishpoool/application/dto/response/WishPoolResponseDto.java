package WishPool.Be.wishpoool.application.dto.response;

import WishPool.Be.wishpoool.domain.WishPoolStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

//홈 화면에서 최신순 3개 조회하는 것
public record WishPoolResponseDto(
        Long wishpoolId,
        WishPoolStatus wishPoolStatus,
        int D_day,
        String imageKey,
        String celebrant,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd", timezone = "Asia/Seoul")
        LocalDate birthDay){}