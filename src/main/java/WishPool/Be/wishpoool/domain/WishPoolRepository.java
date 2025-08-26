package WishPool.Be.wishpoool.domain;

import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WishPoolRepository {
    Optional<WishPool> findById(Long id);
    WishPool save (WishPool wishPool);
    Optional<WishPool> findByShareIdentifier(String shareIdentifier);

    Optional<WishPool> findWithParticipantsAndGiftListById(Long wishpoolId);
}
