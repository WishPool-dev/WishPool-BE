package WishPool.Be.wishpoool.infra.persistence.jpa;

import WishPool.Be.wishpoool.domain.WishPool;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishPoolJpaRepository extends JpaRepository<WishPool, Long> {
}
