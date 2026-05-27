package com.polywave.userservice.application.user.query.service;

import com.polywave.userservice.application.user.query.result.OnboardingReminderUserResult;
import com.polywave.userservice.repository.query.UserSegmentQueryRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 알림 발급 대상 사용자 세그먼트 조회 application service.
 *
 * - InternalUserSegmentController 가 repository 를 직접 호출하지 않고 이 service 를 경유한다.
 *   (다른 controller 들과 동일한 controller → service → repository 레이어 컨벤션)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserSegmentQueryService {

    private final UserSegmentQueryRepository userSegmentQueryRepository;

    public List<OnboardingReminderUserResult> findUsersStuckAtNicknameBefore(Instant cutoff) {
        return userSegmentQueryRepository.findUsersStuckAtNicknameBefore(cutoff);
    }

    public List<OnboardingReminderUserResult> findUsersStuckAtCategoryBefore(Instant cutoff) {
        return userSegmentQueryRepository.findUsersStuckAtCategoryBefore(cutoff);
    }

    public List<OnboardingReminderUserResult> findOnboardingCompletedUsers() {
        return userSegmentQueryRepository.findOnboardingCompletedUsers();
    }
}
