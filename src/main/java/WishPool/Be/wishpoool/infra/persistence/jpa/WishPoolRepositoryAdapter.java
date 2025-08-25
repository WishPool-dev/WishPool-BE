package WishPool.Be.wishpoool.infra.persistence.jpa;

import WishPool.Be.wishpoool.domain.WishPool;
import WishPool.Be.wishpoool.domain.WishPoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}
