package WishPool.Be.wishpoool.presentation;

import WishPool.Be.security.service.SecurityUserDto;
import WishPool.Be.wishpoool.application.command.WishPoolCommandService;
import WishPool.Be.wishpoool.application.dto.request.CreateGiftListRequestDto;
import WishPool.Be.wishpoool.application.dto.request.CreateWishPoolRequestDto;
import WishPool.Be.wishpoool.application.dto.request.StartSelectionRequestDto;
import WishPool.Be.wishpoool.application.dto.response.CelebrantUrlResponseDto;
import WishPool.Be.wishpoool.application.dto.response.CreatedWishPoolResponseDto;
import WishPool.Be.wishpoool.application.dto.response.WishPoolDetailResponseDto;
import WishPool.Be.wishpoool.application.dto.response.WishPoolResponseDto;
import WishPool.Be.wishpoool.application.query.WishPoolQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/wishpools")
@RequiredArgsConstructor
public class WishPoolAuthController {
    private final WishPoolCommandService wishPoolCommandService;
    private final WishPoolQueryService wishPoolQueryService;
    // 위시풀 생성, 링크 생성
    @PostMapping
    public ResponseEntity<CreatedWishPoolResponseDto> createWishPool(@AuthenticationPrincipal SecurityUserDto securityUserDto,
                                                                     @Valid @RequestBody CreateWishPoolRequestDto dto){
        URI uri = URI.create("api/wishpools");
        return ResponseEntity.created(uri).body(wishPoolCommandService.creatWishPool(dto, securityUserDto));
    }

    // 위시풀 조회 - 홈(로그인)
    @GetMapping
    public ResponseEntity<List<WishPoolResponseDto>> getMyRecentTop3Wishpool(@AuthenticationPrincipal SecurityUserDto securityUserDto){
        return ResponseEntity.ok().body(wishPoolQueryService.findWishPoolInfo(securityUserDto.getUserId()));
    }

    // 위시풀 상세 조회
    @GetMapping("/{wishpoolId}")
    public ResponseEntity<WishPoolDetailResponseDto> getDetailWishPool(@AuthenticationPrincipal SecurityUserDto securityUserDto,
                                                                       @PathVariable Long wishpoolId){
        return ResponseEntity.ok().body(wishPoolQueryService.findWishPoolDetail(wishpoolId,securityUserDto.getUserId()));
    }

    // 대표자 나도 참여
    @PostMapping("/join")
    public ResponseEntity<Long> joinWishPool(@AuthenticationPrincipal SecurityUserDto securityUserDto,
                                             @Valid @RequestBody CreateGiftListRequestDto dto){
        URI uri = URI.create("api/wishpools/join");
        return ResponseEntity.created(uri).body(wishPoolCommandService.createGiftListForOwner(securityUserDto, dto));
    }

    // 대표자 링크 생성
    @PatchMapping("/{wishpoolId}/selection")
    public ResponseEntity<CelebrantUrlResponseDto> sendToCelebrant(@AuthenticationPrincipal SecurityUserDto securityUserDto,
                                                                   @PathVariable Long wishpoolId, @RequestBody StartSelectionRequestDto dto){
        return ResponseEntity.ok().body(wishPoolCommandService.getChosenURL(wishpoolId,dto.pickDate(),securityUserDto.getUserId()));
    }

}
