package WishPool.Be.wishpoool.infra.persistence.jpa;

import WishPool.Be.wishpoool.domain.Participant;
import WishPool.Be.wishpoool.domain.ParticipantRepository;
import WishPool.Be.wishpoool.domain.ParticipantRole;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantJpaRepository extends JpaRepository<Participant, Long> {
    //최근 참여한 3개의 위시풀 리스트 조회 createdAt말고 modified도 생각해보면 좋을 듯
    List<Participant> findByUser_UserIdOrderByCreatedDateDesc(Long userId);
    Long countByWishPool_WishPoolId(Long wishpoolId);
    Long countByWishPool_WishPoolIdAndGiftListIsNotNull(Long wishpoolId);
    // wishpool과 owner를 탐색
    Participant findParticipantByWishPool_WishPoolIdAndParticipantRole(Long wishpoolId, ParticipantRole participantRole);
    @EntityGraph(attributePaths = {"wishPool"})
    Participant findParticipantByUser_UserIdAndWishPool_WishPoolId(Long userId, Long wishpoolId);
}
