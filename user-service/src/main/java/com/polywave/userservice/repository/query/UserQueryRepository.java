package com.polywave.userservice.repository.query;

import com.polywave.userservice.application.user.query.result.UserMeResult;
import com.polywave.userservice.application.user.query.result.UserNicknameResult;
import com.polywave.userservice.domain.OnBoardingStatus;
import java.util.Collection;
import java.util.List;

public interface UserQueryRepository {

    boolean existsByNickname(String nickname);

    OnBoardingStatus findOnboardingStatusByUserId(Long userId);

    UserMeResult findUserMeByUserId(Long userId);

    boolean existsValidSession(Long userId, String sessionId);

    /** 주어진 userId 목록에 대해 닉네임을 일괄 반환. 존재하지 않는 id 는 결과에서 제외. */
    List<UserNicknameResult> findNicknamesByUserIds(Collection<Long> userIds);
}