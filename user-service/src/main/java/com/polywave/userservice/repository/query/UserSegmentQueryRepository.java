package com.polywave.userservice.repository.query;

import com.polywave.userservice.application.user.query.result.OnboardingReminderUserResult;
import java.time.Instant;
import java.util.List;

/**
 * 알림 발급 대상 사용자 세그먼트 조회 (user-service 도메인).
 *
 * - user-service 가 직접 보유한 사용자 마일스톤 컬럼을 기준으로 세그먼트를 산출한다.
 * - 결과는 internal API 로 notification-service 에 전달된다.
 *   (notification-service 는 cross-service 호출로만 이 데이터를 받아간다)
 */
public interface UserSegmentQueryRepository {

    /**
     * 행 1: 별명 설정은 완료(nickname_set_at != null)했지만, 관심 주제 선택은 안 한(category_set_at == null) 유저 중
     * 별명 설정 시각이 cutoff 이전인 유저.
     */
    List<OnboardingReminderUserResult> findUsersStuckAtNicknameBefore(Instant cutoff);

    /**
     * 행 2: 관심 주제 선택은 완료(category_set_at != null)했지만, 추가 정보 입력은 안 한(profile_completed_at == null) 유저 중
     * 관심 주제 선택 시각이 cutoff 이전인 유저.
     */
    List<OnboardingReminderUserResult> findUsersStuckAtCategoryBefore(Instant cutoff);

    /**
     * 행 3·4·5 공통 대상: 추가 정보 입력까지 완료한(profile_completed_at != null) 유저.
     * 매일 fan-out broadcast 알림의 발급 대상이 된다.
     */
    List<OnboardingReminderUserResult> findOnboardingCompletedUsers();
}
