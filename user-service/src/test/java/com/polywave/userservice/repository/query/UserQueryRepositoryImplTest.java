package com.polywave.userservice.repository.query;

import com.polywave.userservice.application.user.query.result.UserMeResult;
import com.polywave.userservice.domain.Gender;
import com.polywave.userservice.domain.OnBoardingStatus;
import com.polywave.userservice.domain.User;
import com.polywave.userservice.domain.UserOauth;
import com.polywave.userservice.repository.query.impl.UserQueryRepositoryImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserQueryRepositoryImplTest {

    @Autowired
    private EntityManager em;

    private UserQueryRepository userQueryRepository;

    @BeforeEach
    void setUp() {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);
        userQueryRepository = new UserQueryRepositoryImpl(jpaQueryFactory);
    }

    @Test
    @DisplayName("닉네임 존재 여부 확인 - 존재하는 경우 true 반환")
    void existsByNickname_Exists_ReturnsTrue() {
        // given
        User user = User.builder()
                .nickname("existingNick")
                .onboardingStatus(OnBoardingStatus.ONBOARDING)
                .build();
        em.persist(user);
        em.flush();
        em.clear();

        // when
        boolean result = userQueryRepository.existsByNickname("existingNick");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("닉네임 존재 여부 확인 - 존재하지 않는 경우 false 반환")
    void existsByNickname_NotExists_ReturnsFalse() {
        // when
        boolean result = userQueryRepository.existsByNickname("nonExistingNick");

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("유저 ID로 온보딩 상태 조회 성공")
    void findOnboardingStatusByUserId_Success() {
        // given
        User user = User.builder()
                .nickname("testUser")
                .onboardingStatus(OnBoardingStatus.CATEGORY)
                .build();
        em.persist(user);
        em.flush();
        em.clear();

        // when
        OnBoardingStatus status = userQueryRepository.findOnboardingStatusByUserId(user.getId());

        // then
        assertThat(status).isEqualTo(OnBoardingStatus.CATEGORY);
    }

    @Test
    @DisplayName("유저 ID로 온보딩 상태 조회 - 존재하지 않는 유저의 경우 null 반환")
    void findOnboardingStatusByUserId_NotFound() {
        // when
        OnBoardingStatus status = userQueryRepository.findOnboardingStatusByUserId(999L);

        // then
        assertThat(status).isNull();
    }

    @Test
    @DisplayName("UserMeResult (유저 + OAuth 정보) 조인 조회 성공")
    void findUserMeByUserId_Success() {
        // given
        User user = User.builder()
                .nickname("joinUser")
                .onboardingStatus(OnBoardingStatus.COMPLETE)
                .gender(Gender.MAN)
                .birthDate("19901111")
                .profileImageUrl("http://test.url")
                .sido("서울")
                .sigungu("강남구")
                .emdName("역삼동")
                .address("서울 강남구 역삼동")
                .build();
        em.persist(user);

        UserOauth userOauth = UserOauth.builder()
                .user(user)
                .provider("KAKAO")
                .providerUserId("provider-id-1234")
                .build();
        em.persist(userOauth);

        em.flush();
        em.clear();

        // when
        UserMeResult result = userQueryRepository.findUserMeByUserId(user.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(user.getId());
        assertThat(result.nickname()).isEqualTo("joinUser");
        assertThat(result.onboardingStatus()).isEqualTo(OnBoardingStatus.COMPLETE);
        assertThat(result.provider()).isEqualTo("KAKAO");
        assertThat(result.providerUserId()).isEqualTo("provider-id-1234");
        assertThat(result.gender()).isEqualTo(Gender.MAN);
    }

    @Test
    @DisplayName("UserMeResult 조회 실패 - 해당 ID의 유저가 없는 경우 null 반환")
    void findUserMeByUserId_NotFound() {
        // when
        UserMeResult result = userQueryRepository.findUserMeByUserId(999L);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("UserMeResult 조회 - Oauth 정보가 없을 경우 leftJoin에 의해 유저 정보만 조회(OAuth 필드는 null)")
    void findUserMeByUserId_NoOauth_ReturnsUserOnly() {
        // given
        User user = User.builder()
                .nickname("onlyUser")
                .onboardingStatus(OnBoardingStatus.ONBOARDING)
                .build();
        em.persist(user);
        em.flush();
        em.clear();

        // when
        UserMeResult result = userQueryRepository.findUserMeByUserId(user.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(user.getId());
        assertThat(result.nickname()).isEqualTo("onlyUser");
        assertThat(result.provider()).isNull();
        assertThat(result.providerUserId()).isNull();
    }
}
