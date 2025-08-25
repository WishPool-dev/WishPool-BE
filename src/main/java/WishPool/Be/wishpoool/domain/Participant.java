package WishPool.Be.wishpoool.domain;

import WishPool.Be.user.domain.User;
import WishPool.Be.util.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public static Participant from(WishPool wishPool, User owner){
        Participant participant = new Participant();
        participant.wishPool = wishPool;
        participant.setUser(owner);
        participant.participantRole = ParticipantRole.OWNER;
        participant.guestName = owner.getName();
        return participant;
    }

    public static Participant fromGuest(WishPool wishPool, String guestName){
        Participant participant = new Participant();
        participant.wishPool = wishPool;
        participant.participantRole = ParticipantRole.GUEST;
        participant.guestName = guestName;
        return participant;
    }

    // 로그인된 사용자 연관관계 편의 메소드
    private void setUser(User user){
        this.user = user;
        user.getParticipants().add(this);
    }
}
