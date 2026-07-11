package com.polywave.userservice.domain;

import com.polywave.common.domain.BaseEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Entity
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Column(length = 8)
    private String birthDate;

    @Column(length = 500)
    private String profileImageUrl;

    @Column(length = 20)
    private String sido;

    @Column(length = 20)
    private String sigungu;

    @Column(length = 20)
    private String emdName;

    @Column(length = 100)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private OnBoardingStatus onboardingStatus;

    /**
     * 현재 사용자가 서버에서 인정하는 "유효 세션" 식별자.
     *
     * - 로그인 성공 시 새 sid 발급 후 이 컬럼을 갱신
     * - access/refresh token 모두 동일 sid를 포함
     * - 이후 요청 시 token sid와 이 컬럼 값을 비교하여 단일 기기 로그인 정책을 강제
     */
    @Column(length = 64)
    private String authSessionId;

    /**
     * 온보딩 마일스톤 진입 시각.
     * 한 번 채워지면 갱신되지 않는 immutable 시각으로, D+1 형태 리마인더 알림이
     * 같은 유저에게 반복 발송되는 것을 방지하기 위해 사용한다.
     */
    @Column(name = "nickname_set_at")
    private Instant nicknameSetAt;

    @Column(name = "category_set_at")
    private Instant categorySetAt;

    @Column(name = "profile_completed_at")
    private Instant profileCompletedAt;

    /** 계정 상태. 탈퇴(soft-delete) 시 WITHDRAWN 으로 전이된다. */
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "withdrawn_at")
    private Instant withdrawnAt;

    /** 탈퇴 사유(다중 선택)를 enum 이름 콤마 조합으로 저장. */
    @Column(name = "withdrawal_reasons", columnDefinition = "TEXT")
    private String withdrawalReasons;

    /** 기타(ETC) 사유 직접 입력. 최대 200자. */
    @Column(name = "reason_etc", length = 200)
    private String reasonEtc;

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfile(Gender gender, String birthDate, String sido, String sigungu, String emdName,
            String address) {
        this.gender = gender;
        this.birthDate = birthDate;
        this.sido = sido;
        this.sigungu = sigungu;
        this.emdName = emdName;
        this.address = address;
    }

    public void updateOnBoardingStatus(OnBoardingStatus status) {
        this.onboardingStatus = status;
    }

    public void updateAuthSessionId(String authSessionId) {
        this.authSessionId = authSessionId;
    }

    /** 별명 설정 완료 마일스톤 — 이미 기록되어 있으면 덮어쓰지 않는다. */
    public void markNicknameSetAtIfAbsent(Instant at) {
        if (this.nicknameSetAt == null) {
            this.nicknameSetAt = at;
        }
    }

    /** 관심 주제 선택 완료 마일스톤 — 이미 기록되어 있으면 덮어쓰지 않는다. */
    public void markCategorySetAtIfAbsent(Instant at) {
        if (this.categorySetAt == null) {
            this.categorySetAt = at;
        }
    }

    /** 추가 정보 입력(=온보딩 완료) 마일스톤 — 이미 기록되어 있으면 덮어쓰지 않는다. */
    public void markProfileCompletedAtIfAbsent(Instant at) {
        if (this.profileCompletedAt == null) {
            this.profileCompletedAt = at;
        }
    }

    public boolean isWithdrawn() {
        return this.status == UserStatus.WITHDRAWN;
    }

    /**
     * 회원 탈퇴(soft-delete). 행과 id 는 보존하되 PII 를 말소하고 WITHDRAWN 으로 전이한다.
     * - 닉네임 NULL 처리로 슬롯을 해제(타인이 재사용 가능).
     * - authSessionId NULL 로 잔존 토큰을 무효화.
     * - 온보딩 마일스톤 초기화로 재가입 시 온보딩을 처음부터 진행.
     * - 표결(user_bill_votes)은 user-service 밖이라 여기서 다루지 않으며, uid 보존으로 그대로 연결 유지된다.
     */
    public void withdraw(List<WithdrawalReason> reasons, String etcText) {
        this.status = UserStatus.WITHDRAWN;
        this.withdrawnAt = Instant.now();
        this.withdrawalReasons = (reasons == null || reasons.isEmpty())
                ? null
                : reasons.stream().map(Enum::name).collect(Collectors.joining(","));
        this.reasonEtc = (etcText == null || etcText.isBlank()) ? null : etcText.strip();

        this.nickname = null;
        this.gender = null;
        this.birthDate = null;
        this.profileImageUrl = null;
        this.sido = null;
        this.sigungu = null;
        this.emdName = null;
        this.address = null;

        this.authSessionId = null;

        this.nicknameSetAt = null;
        this.categorySetAt = null;
        this.profileCompletedAt = null;
    }

    /**
     * 탈퇴 후 7일이 지난 같은 소셜 계정의 재가입 — 동일 uid 를 재활성한다.
     * 온보딩을 처음부터 다시 진행하도록 상태를 SIGNUP 으로 리셋한다.
     */
    public void reactivate(String nickname) {
        this.status = UserStatus.ACTIVE;
        this.withdrawnAt = null;
        this.withdrawalReasons = null;
        this.reasonEtc = null;
        this.nickname = nickname;
        this.onboardingStatus = OnBoardingStatus.SIGNUP;
    }
}