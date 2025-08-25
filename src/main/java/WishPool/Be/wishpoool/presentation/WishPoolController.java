package WishPool.Be.wishpoool.presentation;

import WishPool.Be.security.service.SecurityUserDto;
import WishPool.Be.wishpoool.application.command.WishPoolCommandService;
import WishPool.Be.wishpoool.application.dto.request.CreateWishPoolRequestDto;
import WishPool.Be.wishpoool.application.dto.response.CreatedWishPoolResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/api/wishpools")
@RequiredArgsConstructor
public class WishPoolController {
    private final WishPoolCommandService wishPoolCommandService;

    // 위시풀 생성
    @PostMapping
    public ResponseEntity<CreatedWishPoolResponseDto> createWishPool(@AuthenticationPrincipal SecurityUserDto securityUserDto,
                                                                     @Valid @RequestBody CreateWishPoolRequestDto dto){
        URI uri = URI.create("api/wishpools");
        return ResponseEntity.created(uri).body(wishPoolCommandService.creatWishPool(dto, securityUserDto));
    }
}
