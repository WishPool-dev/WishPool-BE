package WishPool.Be.wishpoool.domain;

import WishPool.Be.wishpoool.application.dto.request.GiftItemDto;
import WishPool.Be.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GiftList extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long giftListId;

    @OneToOne(mappedBy = "giftList")
    private Participant participant;

    @OneToMany(mappedBy = "giftList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GiftItem> giftItems = new ArrayList<>();

    public static GiftList createWithItems(Participant participant, List<GiftItemDto> itemDtos){
        GiftList giftList = new GiftList();
        giftList.participant = participant;
        itemDtos.forEach(itemDto -> {
            GiftItem newItem = GiftItem.createItem(itemDto);
            newItem.addGiftList(giftList);
        });
        return giftList;
    }

    void setParticipant(Participant participant){
        this.participant = participant;
    }
}
