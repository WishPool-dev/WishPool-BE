package WishPool.Be.wishpoool.infra.persistence.jpa;

import WishPool.Be.wishpoool.domain.GiftItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GiftItemRepository extends JpaRepository<GiftItem, Long> {
    List<GiftItem> findAllByGiftItemIdIn(List<Long> giftItemIds);
}
