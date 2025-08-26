package WishPool.Be.wishpoool.domain;

import WishPool.Be.wishpoool.application.query.GiftListQueryService;
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
