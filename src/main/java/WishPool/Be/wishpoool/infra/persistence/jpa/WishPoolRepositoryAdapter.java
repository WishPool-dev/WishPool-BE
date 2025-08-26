package WishPool.Be.wishpoool.infra.persistence.jpa;

import WishPool.Be.wishpoool.domain.WishPool;
import WishPool.Be.wishpoool.domain.WishPoolRepository;
import WishPool.Be.wishpoool.domain.WishPoolStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class WishPoolRepositoryAdapter implements WishPoolRepository {
    private final WishPoolJpaRepository wishPoolJpaRepository;

    @Override
    public Optional<WishPool> findById(Long id) {
        return wishPoolJpaRepository.findById(id);
    }

    @Override
    public WishPool save(WishPool wishPool) {
        return wishPoolJpaRepository.save(wishPool);
    }

    @Override
    public Optional<WishPool> findWithParticipantsAndGiftListById(Long wishpoolId) {
        return wishPoolJpaRepository.findWithParticipantsAndGiftListById(wishpoolId);
    }

    @Override
    public Optional<WishPool> findByShareIdentifier(String shareIdentifier) {
        return wishPoolJpaRepository.findWishPoolByShareIdentifier(shareIdentifier);
    }

    @Override
    public Optional<WishPool> findByChosenIdentifier(String chosenIdentifier) {
        return wishPoolJpaRepository.findWishPoolByChosenIdentifier(chosenIdentifier);
    }

    @Override
    public List<WishPool> findAllByWishPoolStatusAndParticipantEndDate(WishPoolStatus status, LocalDate today) {
        return wishPoolJpaRepository.findAllByWishPoolStatusAndParticipantEndDate(status, today);
    }
}
