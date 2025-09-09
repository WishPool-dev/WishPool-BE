package WishPool.Be.wishpoool.infra.persistence.jpa;

import WishPool.Be.wishpoool.domain.GiftList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftListRepository extends JpaRepository<GiftList, Long> {
    @Query("SELECT DISTINCT gl FROM GiftList gl " +
            "LEFT JOIN FETCH gl.giftItems gi " +
            "WHERE gl IN :giftLists")
    List<GiftList> findWithGiftItemsByIn(List<GiftList> giftLists);
}
