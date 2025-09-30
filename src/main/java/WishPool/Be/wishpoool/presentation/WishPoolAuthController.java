package WishPool.Be.wishpoool.presentation;

import WishPool.Be.security.service.SecurityUserDto;
import WishPool.Be.wishpoool.application.command.WishPoolCommandService;
import WishPool.Be.wishpoool.application.dto.request.CreateGiftListRequestDto;
import WishPool.Be.wishpoool.application.dto.request.CreateWishPoolRequestDto;
import WishPool.Be.wishpoool.application.dto.request.StartSelectionRequestDto;
import WishPool.Be.wishpoool.application.dto.request.WishpoolUpdateRequestDto;
import WishPool.Be.wishpoool.application.dto.response.CelebrantUrlResponseDto;
import WishPool.Be.wishpoool.application.dto.response.CreatedWishPoolResponseDto;
import WishPool.Be.wishpoool.application.dto.response.WishPoolDetailResponseDto;
import WishPool.Be.wishpoool.application.dto.response.WishPoolResponseDto;
import WishPool.Be.wishpoool.application.query.WishPoolQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
@Tag(name = "인증된 사용자의 위시풀 API", description = "로그인 후 위시풀 생성, 조회, 참여 등 인증이 필요한 API")
@RestController
@RequestMapping("/api/wishpools")
@RequiredArgsConstructor
public class WishPoolAuthController {
    private final WishPoolCommandService wishPoolCommandService;
    private final WishPoolQueryService wishPoolQueryService;

    @Operation(summary = "위시풀 생성", description = "대표자가 새로운 위시풀을 생성합니다.")
    @PostMapping
    public ResponseEntity<CreatedWishPoolResponseDto> createWishPool(
            @Parameter(hidden = true) @AuthenticationPrincipal SecurityUserDto securityUserDto,
            @Valid @RequestBody CreateWishPoolRequestDto dto){
        URI uri = URI.create("api/wishpools");
        return ResponseEntity.created(uri).body(wishPoolCommandService.creatWishPool(dto, securityUserDto));
    }

    @Operation(summary = "내 위시풀 목록 조회 (홈)", description = "로그인한 사용자가 참여하고 있는 위시풀 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<WishPoolResponseDto>> getMyRecentTop3Wishpool(
            @Parameter(hidden = true) @AuthenticationPrincipal SecurityUserDto securityUserDto){
        return ResponseEntity.ok().body(wishPoolQueryService.findWishPoolInfo(securityUserDto.getUserId()));
    }

    @Operation(summary = "위시풀 상세 조회", description = "특정 위시풀의 상세 정보를 조회합니다.")
    @GetMapping("/{wishpoolId}")
    public ResponseEntity<WishPoolDetailResponseDto> getDetailWishPool(
            @Parameter(hidden = true) @AuthenticationPrincipal SecurityUserDto securityUserDto,
            @Parameter(description = "상세 조회할 위시풀의 ID", example = "1") @PathVariable Long wishpoolId){
        return ResponseEntity.ok().body(wishPoolQueryService.findWishPoolDetail(wishpoolId, securityUserDto.getUserId()));
    }

    @Operation(summary = "대표자 위시풀 참여", description = "위시풀을 생성한 대표자가 직접 선물 등록으로 참여합니다. (나도 참여하기)")
    @PostMapping("/join")
    public ResponseEntity<Long> joinWishPool(
            @Parameter(hidden = true) @AuthenticationPrincipal SecurityUserDto securityUserDto,
            @Valid @RequestBody CreateGiftListRequestDto dto){
        URI uri = URI.create("api/wishpools/join");
        return ResponseEntity.created(uri).body(wishPoolCommandService.createGiftListForOwner(securityUserDto, dto));
    }

    @Operation(summary = "생일자 선물 선택 링크 생성", description = "대표자가 선물 선택 마감일을 설정하고, 생일자에게 전달할 선물 확인용 고유 링크를 생성합니다.")
    @PatchMapping("/{wishpoolId}/selection")
    public ResponseEntity<CelebrantUrlResponseDto> sendToCelebrant(
            @Parameter(hidden = true) @AuthenticationPrincipal SecurityUserDto securityUserDto,
            @Parameter(description = "링크를 생성할 위시풀의 ID", example = "1") @PathVariable Long wishpoolId,
            @RequestBody StartSelectionRequestDto dto){
        return ResponseEntity.ok().body(wishPoolCommandService.getChosenURL(wishpoolId, dto.pickDate(), securityUserDto.getUserId()));
    }

    @Operation(summary = "위시풀 수정", description = "대표자가 선물 선택 마감일을 설정하고, 생일자에게 전달할 선물 확인용 고유 링크를 생성합니다.")
    @PatchMapping("/{wishpoolId}")
    public ResponseEntity<WishpoolUpdateRequestDto> updateWishPool(
            @AuthenticationPrincipal SecurityUserDto securityUserDto,
            @RequestBody WishpoolUpdateRequestDto dto,
            @PathVariable Long wishpoolId){
        return ResponseEntity.ok().body(wishPoolCommandService.updateWishPool(wishpoolId, securityUserDto.getUserId(), dto));
    }

}