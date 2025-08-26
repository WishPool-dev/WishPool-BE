package WishPool.Be.wishpoool.presentation;

import WishPool.Be.wishpoool.application.dto.request.CreateGiftListRequestDto;
import WishPool.Be.wishpoool.application.command.WishPoolCommandService;
import WishPool.Be.wishpoool.application.dto.response.WishPoolGuestInfoResponseDto;
import WishPool.Be.wishpoool.application.dto.response.gift.BirthdayGiftsViewResponseDto;
import WishPool.Be.wishpoool.application.dto.response.gift.GiftListResponseDto;
import WishPool.Be.wishpoool.application.query.GiftListQueryService;
import WishPool.Be.wishpoool.application.query.WishPoolQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/wishpools")
@RequiredArgsConstructor
public class WishPoolController {
    private final WishPoolQueryService wishPoolQueryService;
    private final WishPoolCommandService wishPoolCommandService;
    private final GiftListQueryService giftListQueryService;

    // 링크타고 게스트가 조회
    @GetMapping("/{shareIdentifier}")
    public ResponseEntity<WishPoolGuestInfoResponseDto> getWishPoolGuestInfo(@PathVariable String shareIdentifier){
        return ResponseEntity.ok().body(wishPoolQueryService.getGuestInfo(shareIdentifier));
    }

    // 게스트의 위시풀 참여
    @PostMapping("/guests")
    public ResponseEntity<Long> joinWishPoolWithGuest(@RequestBody CreateGiftListRequestDto dto){
        Long createdWishpoolId = wishPoolCommandService.createGiftListForGuest(dto);
        URI uri = URI.create("/wishpools/guests");
        return ResponseEntity.created(uri).body(createdWishpoolId);
    }

    // 선물 리스트 확인
    @GetMapping("/gifts/{wishpoolId}")
    public ResponseEntity<List<GiftListResponseDto>> getGiftLists(@PathVariable Long wishpoolId){
        return ResponseEntity.ok().body(giftListQueryService.getAllGifts(wishpoolId));
    }

    // 생일자가 링크로 선물이랑 문구 받아보기
    @GetMapping("/celebrant/{chosenIdentifier}")
    public ResponseEntity<BirthdayGiftsViewResponseDto> joinWishPoolWithGuest(@PathVariable String chosenIdentifier){
        return ResponseEntity.ok().body(giftListQueryService.getGiftsForBirthdayOwner(chosenIdentifier));
    }
}
