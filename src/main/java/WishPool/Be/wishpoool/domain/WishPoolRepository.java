package WishPool.Be.wishpoool.domain;

import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WishPoolRepository {
    Optional<WishPool> findById(Long id);
    WishPool save (WishPool wishPool);
    Optional<WishPool> findByShareIdentifier(String shareIdentifier);
    Optional<WishPool> findByChosenIdentifier(String chosenIdentifier);
    Optional<WishPool> findByCompletedIdentifier(String completeIdentifier);

    Optional<WishPool> findWithParticipantsAndGiftListById(Long wishpoolId);

    List<WishPool> findAllByWishPoolStatusAndParticipantEndDate(WishPoolStatus open, LocalDate yesterday);
    void delete(WishPool wishPool);
}
