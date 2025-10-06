package WishPool.Be.wishpoool.domain;

import WishPool.Be.global.exception.business.BusinessException;
import WishPool.Be.global.exception.business.ErrorStatus;
import WishPool.Be.wishpoool.application.dto.request.CreateGiftListRequestDto;
import WishPool.Be.user.domain.User;
import WishPool.Be.util.BaseEntity;
import WishPool.Be.wishpoool.application.dto.request.CreateWishPoolRequestDto;
import WishPool.Be.wishpoool.application.dto.request.WishpoolUpdateRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WishPool extends BaseEntity {
    @Id
    @Column(name = "wishpool_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishPoolId;

    //생일자 이름
    @Column(nullable = false)
    private String celebrant;

    @Column(name = "birth_day",nullable = false)
    private LocalDate birthDay;

    @Lob @Column(nullable = false)
    private String description;

    @Column(name = "image_key")
    private String imageKey;

    // 참여자 마감일
    @Column(name = "participant_end_date")
    private LocalDate participantEndDate;

    // 생일자 픽 마감일
    @Column(name = "celebrant_pick_end_date")
    private LocalDate celebrantPickEndDate;

    @Column(name = "wishpool_status")
    @Enumerated(EnumType.STRING)
    private WishPoolStatus wishPoolStatus;

    // 공유 링크에 들어갈 식별자, 있으면 리트
    @Column(name = "share_identifier",unique = true)
    private String shareIdentifier;

    // 나중에 생일자에게 전달할 링크
    @Column(name = "chosen_identifier",unique = true)
    private String chosenIdentifier;

    // 나중에 생일자에게 전달할 링크
    @Column(name = "complete_identifier",unique = true)
    private String completeIdentifier;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Builder.Default
    @OneToMany(mappedBy = "wishPool", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "wishPool", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SelectedGift> selectedGifts = new ArrayList<>();

    public static WishPool createWishPool(CreateWishPoolRequestDto dto, User owner, String shareIdentifier, String chosenIdentifier, String completeIdentifierIdentifier){
        WishPool wishPool = WishPool.builder()
                .celebrant(dto.celebrant())
                .birthDay(dto.birthDay())
                .description(dto.description())
                .imageKey(dto.imageKey())
                .participantEndDate(dto.endDate())
                .shareIdentifier(shareIdentifier)
                .wishPoolStatus(WishPoolStatus.OPEN)
                .ownerName(owner.getName())
                .chosenIdentifier(chosenIdentifier)
                .completeIdentifier(completeIdentifierIdentifier)
                .build();
        Participant organizer = Participant.fromOwner(wishPool, owner);
        wishPool.addParticipant(organizer);
        return wishPool;
    }

    public String startGiftSelection(LocalDate pickDate) {
        // 1. 상태 검증: PENDING 상태가 맞는지 스스로 확인
        if (this.wishPoolStatus != WishPoolStatus.PENDING) {
            throw new BusinessException(ErrorStatus.WISHPOOL_NOT_IN_PENDING_STATE);
        }
        // 2. 날짜 유효성 검증: 참여 마감일 이후인지 스스로 확인
        if (pickDate.isBefore(this.participantEndDate)) {
            throw new BusinessException(ErrorStatus.INVALID_PICK_END_DATE);
        }
        // 3. 상태 변경: 자신의 상태와 데이터를 직접 변경
        this.celebrantPickEndDate = pickDate;
        this.wishPoolStatus = WishPoolStatus.WAITING;
        // 4. 결과 반환
        return this.chosenIdentifier;
    }

    public void createSelectedGift(List<GiftItem> giftItems){
        for (GiftItem giftItem : giftItems) {
            SelectedGift selectedGift = SelectedGift.create(this, giftItem);
            this.selectedGifts.add(selectedGift);
        }
    }

    // 스케줄러에서 PENDING으로 변경
    public void changeStatusToPending(){
        this.wishPoolStatus = WishPoolStatus.PENDING;
    }

    // 대표자의 위시풀 즉시 마감
    public void changeStatusAndEndDate(){
        this.wishPoolStatus = WishPoolStatus.PENDING;
        this.participantEndDate = LocalDate.now();
    }

    // 사용자 선물 선택 시 COMPLETED 변경
    public void changeStatusToCompleted(){
        this.wishPoolStatus = WishPoolStatus.COMPLETED;
    }

    // 위시풀에 비로그인 참여자 참가
    public Participant addGuest(CreateGiftListRequestDto dto){
        Participant guest = Participant.fromGuest(this, dto.guestName(), dto.giftItemDto());
        this.addParticipant(guest);
        return guest;
    }

    // 위시풀 수정하기(
    public void updateWishpool(WishpoolUpdateRequestDto dto){
        if (dto.celebrant() != null) {
            this.celebrant = dto.celebrant();
        }
        if (dto.birthDay() != null) {
            this.birthDay = dto.birthDay();
        }
        if (dto.description() != null) {
            this.description = dto.description();
        }
        if (dto.endDate() != null) {
            this.participantEndDate = dto.endDate();
        }
        if (dto.imageKey() != null) {
            this.imageKey = dto.imageKey();
        }
    }

    // 연관관계 편의 메소드
    private void addParticipant(Participant participant){
        this.participants.add(participant);
    }
}
