package WishPool.Be.wishpoool.domain;

import WishPool.Be.user.domain.User;
import WishPool.Be.util.BaseEntity;
import WishPool.Be.wishpoool.application.dto.request.CreateWishPoolRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class WishPool extends BaseEntity {
    @Id
    @Column(name = "wishpool_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishPoolId;

    @Column(nullable = false)
    private String celebrant;

    @Column(name = "birth_day",nullable = false)
    private LocalDate birthDay;

    @Lob @Column(nullable = false)
    private String description;

    @Column(name = "image_key")
    private String imageKey;

    // 참여자 마감일
    @Column(name = "participant_end_date")
    private LocalDate participantEndDate;

    // 생일자 픽 마감일
    @Column(name = "celebrant_pick_end_date")
    private LocalDate celebrantPickEndDate;

    @Column(name = "wishpool_status")
    @Enumerated(EnumType.STRING)
    private WishPoolStatus wishPoolStatus;

    // 공유 링크에 들어갈 식별자, 있으면 리트
    @Column(name = "share_identifier",unique = true)
    private String shareIdentifier;

    @Builder.Default
    @OneToMany(mappedBy = "wishPool", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    public static WishPool createWishPool(CreateWishPoolRequestDto dto, User owner, String shareIdentifier){
        WishPool wishPool = WishPool.builder()
                .celebrant(dto.celebrant())
                .birthDay(dto.birthDay())
                .description(dto.description())
                .imageKey(dto.imageKey())
                .participantEndDate(dto.endDate())
                .shareIdentifier(shareIdentifier)
                .wishPoolStatus(WishPoolStatus.ACTIVE)
                .build();
        Participant organizer = Participant.from(wishPool, owner);
        wishPool.addParticipant(organizer);
        return wishPool;
    }

    public void addGuest(String guestName){
        Participant guest = Participant.fromGuest(this, guestName);
        this.addParticipant(guest);
    }

    private void addParticipant(Participant participant){
        this.participants.add(participant);
    }
}
