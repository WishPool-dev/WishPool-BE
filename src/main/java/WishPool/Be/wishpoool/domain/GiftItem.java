package WishPool.Be.wishpoool.domain;

import WishPool.Be.wishpoool.application.dto.request.GiftItemDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Builder
public class GiftItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long giftItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gift_list_id")
    private GiftList giftList;

    @Column(name = "item_url", nullable = false)
    private String itemUrl;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    public static GiftItem createItem(GiftItemDto giftItemDto){
        return GiftItem.builder()
                .itemUrl(giftItemDto.itemUrl())
                .itemName(giftItemDto.itemName())
                .build();
    }
    // 연관관계 편의 메소드
    public void addGiftList(GiftList giftList){
        this.giftList = giftList;
        giftList.getGiftItems().add(this);
    }
}
