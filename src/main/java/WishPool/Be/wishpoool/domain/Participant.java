package WishPool.Be.wishpoool.domain;

import WishPool.Be.wishpoool.application.dto.request.CreateGiftListRequestDto;
import WishPool.Be.wishpoool.application.dto.request.GiftItemDto;
import WishPool.Be.user.domain.User;
import WishPool.Be.util.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Participant extends BaseEntity {

    @Column(name = "participant_id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishpool_id")
    private WishPool wishPool;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "guest_name", nullable = true)
    private String guestName;

    @Enumerated(EnumType.STRING)
    private ParticipantRole participantRole;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "gift_list_id", unique = true, nullable = true)
    private GiftList giftList;

    public static Participant fromOwner(WishPool wishPool, User owner){
        Participant participant = new Participant();
        participant.wishPool = wishPool;
        participant.setUser(owner);
        participant.participantRole = ParticipantRole.OWNER;
        participant.guestName = owner.getName();
        return participant;
    }

    public static Participant fromGuest(WishPool wishPool, String guestName, List<GiftItemDto> items){
        Participant participant = new Participant();
        participant.wishPool = wishPool;
        participant.participantRole = ParticipantRole.GUEST;
        participant.guestName = guestName;
        GiftList guestList = GiftList.createWithItems(participant, items);
        participant.linkGiftList(guestList);
        return participant;
    }

    // User가 owner가 맞는지는 서비스에서 검증
    public void addGiftListByOwner(CreateGiftListRequestDto dto){
        GiftList ownerList = GiftList.createWithItems(this,dto.giftItemDto());
        this.linkGiftList(ownerList);
    }

    private void linkGiftList(GiftList giftList) {
        this.giftList = giftList;
        giftList.setParticipant(this);
    }

    // 로그인된 사용자 연관관계 편의 메소드
    private void setUser(User user){
        this.user = user;
        user.getParticipants().add(this);
    }
}
