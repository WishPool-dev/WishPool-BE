package WishPool.Be.wishpoool.infra.persistence.jpa;

import WishPool.Be.wishpoool.domain.WishPool;
import WishPool.Be.wishpoool.domain.WishPoolStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WishPoolJpaRepository extends JpaRepository<WishPool, Long> {
    public Optional<WishPool> findWishPoolByShareIdentifier(String shareIdentifier);

    @Query("SELECT DISTINCT w FROM WishPool w " +
            "JOIN FETCH w.participants p " +
            "LEFT JOIN FETCH p.giftList gl " +
            "WHERE w.wishPoolId = :wishpoolId")
    Optional<WishPool> findWithParticipantsAndGiftListById(Long wishpoolId);

    List<WishPool> findAllByWishPoolStatusAndParticipantEndDate(WishPoolStatus status, LocalDate endDate);
}
