package WishPool.Be.wishpoool.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectedGift {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long selectedGiftId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishpool_id", unique = true, nullable = true)
    private WishPool wishPool;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gift_item_id")
    private GiftItem giftItem;

    public static SelectedGift create(WishPool wishPool, GiftItem giftItem){
        SelectedGift selectedGift = new SelectedGift();
        selectedGift.wishPool = wishPool;
        selectedGift.giftItem = giftItem;
        return selectedGift;
    }
}
