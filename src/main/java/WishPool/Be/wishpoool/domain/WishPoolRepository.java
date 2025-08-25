package WishPool.Be.wishpoool.domain;

import java.util.Optional;

public interface WishPoolRepository {
    Optional<WishPool> findById(Long id);

    WishPool save (WishPool wishPool);
}
