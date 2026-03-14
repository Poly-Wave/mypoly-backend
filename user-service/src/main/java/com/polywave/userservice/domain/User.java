package com.polywave.userservice.domain;

import com.polywave.common.domain.BaseEntity;
import jakarta.persistence.*;
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
}