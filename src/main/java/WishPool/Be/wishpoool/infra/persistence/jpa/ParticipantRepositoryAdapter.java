package WishPool.Be.wishpoool.infra.persistence.jpa;

import WishPool.Be.wishpoool.domain.Participant;
import WishPool.Be.wishpoool.domain.ParticipantRepository;
import WishPool.Be.wishpoool.domain.ParticipantRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ParticipantRepositoryAdapter implements ParticipantRepository{
    private final ParticipantJpaRepository participantJpaRepository;

    @Override
    public List<Participant> findRecentParticipant(Long userId) {
        return participantJpaRepository.findByUser_UserIdOrderByCreatedDateDesc(userId);
    }

    @Override
    public Long getParticipantCount(Long wishpoolId) {
        return participantJpaRepository.countByWishPool_WishPoolId(wishpoolId);
    }

    @Override
    public Participant findWishPoolOwner(Long wishpoolId, ParticipantRole participantRole) {
        return participantJpaRepository.findParticipantByWishPool_WishPoolIdAndParticipantRole(wishpoolId, participantRole);
    }

    @Override
    public Participant save(Participant participant) {
        return participantJpaRepository.save(participant);
    }

    @Override
    public Participant findParticipantByUserAndWishPool(Long userId, Long wishpoolId) {
        return participantJpaRepository.findParticipantByUser_UserIdAndWishPool_WishPoolId(userId, wishpoolId);
    }
}
