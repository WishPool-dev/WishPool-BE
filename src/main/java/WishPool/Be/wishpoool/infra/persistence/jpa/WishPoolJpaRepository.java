package WishPool.Be.wishpoool.infra.persistence.jpa;

import WishPool.Be.wishpoool.domain.WishPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface WishPoolJpaRepository extends JpaRepository<WishPool, Long> {
    public Optional<WishPool> findWishPoolByShareIdentifier(String shareIdentifier);

    @Query("SELECT DISTINCT w FROM WishPool w " +
            "JOIN FETCH w.participants p " +
            "LEFT JOIN FETCH p.giftList gl " +
            "WHERE w.wishPoolId = :wishpoolId")
    Optional<WishPool> findWithParticipantsAndGiftListById(Long wishpoolId);
}
