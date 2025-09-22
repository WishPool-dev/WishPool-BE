package WishPool.Be.contact;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.HashMap;
import java.util.Map;

@Service
public class DiscordWebhookService {

    // WebClient를 사용합니다. 생성자에서 초기화해주는 것이 좋습니다.
    private final WebClient webClient;

    public DiscordWebhookService(WebClient.Builder webClientBuilder,
                                 @Value("${discord.webhook.url}") String webhookUrl) {
        this.webClient = webClientBuilder.baseUrl(webhookUrl).build();
    }


    public void sendMessage(ContactRequestDto contactRequestDto) {
        // Discord 웹훅에 보낼 메시지 본문을 구성합니다.
        Map<String, Object> body = new HashMap<>();
        String content = "새로운 후기가 도착했습니다! 📬\n\n**별점:** " + contactRequestDto.rating() + "\n**내용:** " + contactRequestDto.content();
        body.put("content", content);

        // WebClient를 사용하여 비동기 POST 요청을 보냅니다.
        webClient.post() // POST 메소드
                .contentType(MediaType.APPLICATION_JSON) // JSON 컨텐츠 타입 설정
                .bodyValue(body) // 요청 본문(body) 설정
                .retrieve() // 응답을 받아옴
                .bodyToMono(String.class) // 응답 본문을 Mono<String> 형태로 변환
                .subscribe(); // 비동기 요청을 실제로 실행 (결과를 당장 사용하지 않으므로 subscribe()만 호출)
    }
}