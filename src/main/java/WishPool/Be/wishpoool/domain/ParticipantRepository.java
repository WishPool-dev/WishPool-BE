package WishPool.Be.wishpoool.domain;

import java.util.List;

public interface ParticipantRepository {
    List<Participant> findRecentParticipant(Long userId);

    Long getParticipantCount(Long wishpoolId);

    Participant findWishPoolOwner(Long wishpoolId, ParticipantRole participantRole);

    Participant save(Participant participant);

    Participant findParticipantByUserAndWishPool(Long userId, Long wishpoolId);
}
