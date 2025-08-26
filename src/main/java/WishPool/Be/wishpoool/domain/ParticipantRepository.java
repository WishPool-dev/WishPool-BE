package WishPool.Be.wishpoool.domain;

import java.util.List;

public interface ParticipantRepository {
    List<Participant> findTop3RecentParticipant(Long userId);

    Long getParticipantCount(Long wishpoolId);

    Participant findWishPoolOwner(Long wishpoolId, ParticipantRole participantRole);

    Participant save(Participant participant);
}
