package WishPool.Be.wishpoool.application.query;

import WishPool.Be.global.exception.business.BusinessException;
import WishPool.Be.global.exception.business.ErrorStatus;
import WishPool.Be.user.domain.User;
import WishPool.Be.user.domain.UserRepository;
import WishPool.Be.wishpoool.application.dto.response.WishPoolDetailResponseDto;
import WishPool.Be.wishpoool.application.dto.response.WishPoolGuestInfoResponseDto;
import WishPool.Be.wishpoool.application.dto.response.WishPoolResponseDto;
import WishPool.Be.wishpoool.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishPoolQueryService {
    private final ParticipantRepository participantRepository;
    private final WishPoolRepository wishPoolRepository;
    private final UserRepository userRepository;

    /**
     * 메인 화면에서 위시풀 조회하기, TOP3
     * 위시풀의 상태가 ACTIVE일 때, GIFT_SELECTION일 때는 마감 날짜 계산해야함
     * 위시풀의 상태가 CANCELED, CLOSED일 땐 날짜 없어도 됨
     */
    @Transactional(readOnly = true)
    public List<WishPoolResponseDto> findWishPoolInfo(Long userId){
        User findUser = userRepository.findById(userId).orElseThrow(()-> new BusinessException(ErrorStatus.USER_NOT_FOUND));
        return participantRepository.findTop3RecentParticipant(userId).stream().map(
                participant -> {
                    int remainingDays = calculateEndDate(participant.getWishPool());
                    return new WishPoolResponseDto(
                            participant.getWishPool().getWishPoolId(),
                            participant.getWishPool().getWishPoolStatus(),
                            remainingDays,
                            participant.getWishPool().getImageKey()
                    );
                }
        ).toList();
    }

    // 로그인한 사용자가 상세 조회 - 대표자 ver
    @Transactional(readOnly = true)
    public WishPoolDetailResponseDto findWishPoolDetail(Long wishpoolId, Long userId){
        WishPool wishPool = wishPoolRepository.findById(wishpoolId).orElseThrow(()-> new BusinessException(ErrorStatus.WISHPOOL_NOT_FOUND));
        Long joinCount = participantRepository.getParticipantCount(wishpoolId);
        int d_day = calculateEndDate(wishPool);
        return new WishPoolDetailResponseDto(wishPool, joinCount, d_day);
    }

    // 게스트 info, 재사용
    @Transactional(readOnly = true)
    public WishPoolGuestInfoResponseDto getGuestInfo(String shareIdentifier){
        WishPool wishPool = wishPoolRepository.findByShareIdentifier(shareIdentifier).orElseThrow(()-> new BusinessException(ErrorStatus.WISHPOOL_NOT_FOUND));
        return new WishPoolGuestInfoResponseDto(wishPool);
    }

    // 날짜 계산해주는 헬퍼 메소드
    private int calculateEndDate(WishPool wishPool){
        WishPoolStatus wishPoolStatus = wishPool.getWishPoolStatus();
        // 종료되거나 삭제된 위시풀은 마감일 0
        if(wishPoolStatus == WishPoolStatus.COMPLETED){
            return 0;
        }
        // 참여 마감일
        else if(wishPoolStatus == WishPoolStatus.OPEN){
            return (int) ChronoUnit.DAYS.between(LocalDate.now(), wishPool.getParticipantEndDate());
        }
        // 픽 마감일
        else return (int) ChronoUnit.DAYS.between(LocalDate.now(), wishPool.getCelebrantPickEndDate());
    }
}
