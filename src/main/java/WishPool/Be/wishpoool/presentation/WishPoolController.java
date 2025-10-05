package WishPool.Be.wishpoool.presentation;

import WishPool.Be.wishpoool.application.dto.request.CelebrantPickGifts;
import WishPool.Be.wishpoool.application.dto.request.CreateGiftListRequestDto;
import WishPool.Be.wishpoool.application.command.WishPoolCommandService;
import WishPool.Be.wishpoool.application.dto.response.GuestSharedDto;
import WishPool.Be.wishpoool.application.dto.response.WishPoolGuestInfoResponseDto;
import WishPool.Be.wishpoool.application.dto.response.gift.BirthdayGiftsViewResponseDto;
import WishPool.Be.wishpoool.application.dto.response.gift.GiftListResponseDto;
import WishPool.Be.wishpoool.application.query.GiftListQueryService;
import WishPool.Be.wishpoool.application.query.WishPoolQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
@Tag(name = "위시풀 API", description = "위시풀 조회, 참여, 선물 확인 등 관련 모든 기능을 제공합니다.")
@RestController
@RequestMapping("/wishpools")
@RequiredArgsConstructor
public class WishPoolController {
    private final WishPoolQueryService wishPoolQueryService;
    private final WishPoolCommandService wishPoolCommandService;
    private final GiftListQueryService giftListQueryService;

    @Operation(summary = "게스트용 위시풀 정보 조회", description = "공유 링크 식별자를 통해 게스트가 위시풀의 기본 정보를 조회합니다.")
    @GetMapping("/{shareIdentifier}")
    public ResponseEntity<WishPoolGuestInfoResponseDto> getWishPoolGuestInfo(
            @Parameter(description = "위시풀 공유 식별자", example = "a1b2c3d4e5") @PathVariable String shareIdentifier){
        return ResponseEntity.ok().body(wishPoolQueryService.getGuestInfo(shareIdentifier));
    }

    @Operation(summary = "게스트 위시풀 참여 및 선물 등록", description = "게스트가 위시풀에 참여하며 이름과 선물 정보를 등록합니다.")
    @PostMapping("/guests")
    public ResponseEntity<Long> joinWishPoolWithGuest(@RequestBody CreateGiftListRequestDto dto){
        Long createdWishpoolId = wishPoolCommandService.createGiftListForGuest(dto);
        URI uri = URI.create("/wishpools/guests");
        return ResponseEntity.created(uri).body(createdWishpoolId);
    }

    @Operation(summary = "등록된 선물 리스트 전체 조회", description = "특정 위시풀 ID에 해당하는 모든 선물 목록을 조회합니다.")
    @GetMapping("/gifts/{wishpoolId}")
    public ResponseEntity<GiftListResponseDto> getGiftLists(
            @Parameter(description = "조회할 위시풀의 ID", example = "1") @PathVariable Long wishpoolId){
        return ResponseEntity.ok().body(giftListQueryService.getAllGifts(wishpoolId));
    }

    @Operation(summary = "생일자 본인의 선물 리스트 조회", description = "생일자가 고유 식별자를 통해 등록된 선물과 메시지를 확인합니다.")
    @GetMapping("/celebrant/{chosenIdentifier}")
    public ResponseEntity<BirthdayGiftsViewResponseDto> getGiftsForBirthdayOwner(
            @Parameter(description = "생일자 확인용 식별자", example = "f6g7h8i9j0") @PathVariable String chosenIdentifier){
        return ResponseEntity.ok().body(giftListQueryService.getGiftsForBirthdayOwner(chosenIdentifier));
    }

    @Operation(summary = "생일자 본인의 선물 리스트 선택",
            description = "생일자가 위시풀 리스트 중 마음에 드는 선물을 선택합니다. " +
                    "기존 선물 리스트에 선물 id가 포함되어있기 때문에 사용자가 선택한 선물들만 body에 담아주시면 됩니다.")
    @PostMapping("/celebrant/{wishpoolId}")
    public ResponseEntity<Long> selectGiftsByBirthdayOwner(
            @Parameter(description = "생일자 확인용 식별자", example = "f6g7h8i9j0") @PathVariable Long wishpoolId, @RequestBody CelebrantPickGifts giftItemIds){
        return ResponseEntity.ok().body(wishPoolCommandService.selectGiftsByCelebrant(wishpoolId, giftItemIds.gifts()));
    }

    @Operation(summary = "게스트 링크 생성",
            description = "게스트에게 링크를 전달합니다. 해당 shareIdentifier 응답의 값을 사용하여 GET 요청을 보내면 wishpool의 정보를 확인할 수 있습니다.")
    @GetMapping("/guest/{wishpoolId}/shared")
    public ResponseEntity<GuestSharedDto> getGuestsLink(
            @Parameter(description = "위시풀 ID", example = "1") @PathVariable Long wishpoolId){
        return ResponseEntity.ok().body(wishPoolQueryService.getSharedLink(wishpoolId));
    }
}