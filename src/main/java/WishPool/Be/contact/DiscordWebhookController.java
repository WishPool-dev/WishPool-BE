package WishPool.Be.contact;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DiscordWebhookController {
    private final DiscordWebhookService discordWebhookService;

    @PostMapping("/contact")
    public ResponseEntity<Void> regRating(@RequestBody ContactRequestDto contactRequestDto){
        discordWebhookService.sendMessage(contactRequestDto);
        return ResponseEntity.ok().build();
    }
}
