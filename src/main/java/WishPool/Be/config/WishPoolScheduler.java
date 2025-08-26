package WishPool.Be.config;

import WishPool.Be.wishpoool.application.command.WishPoolCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class WishPoolScheduler {

    private final WishPoolCommandService wishPoolCommandService;

    // 매일 자정(00:00:00)에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void closeExpiredWishPools() {
        // 어제 날짜를 기준으로 마감된 위시풀을 찾아서 상태 변경
        LocalDate yesterday = LocalDate.now().minusDays(1);
        wishPoolCommandService.changeOpenWishPoolsToPending(yesterday);
    }
}
