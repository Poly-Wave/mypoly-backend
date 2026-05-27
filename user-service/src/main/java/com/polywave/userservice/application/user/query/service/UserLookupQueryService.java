package com.polywave.userservice.application.user.query.service;

import com.polywave.userservice.application.user.query.result.UserNicknameResult;
import com.polywave.userservice.repository.query.UserQueryRepository;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 다른 서비스가 사용자 정보를 lookup 할 때 사용하는 internal 조회 application service.
 *
 * - InternalUserLookupController 가 repository 를 직접 호출하지 않고 이 service 를 경유한다.
 *   (다른 controller 들과 동일한 controller → service → repository 레이어 컨벤션)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserLookupQueryService {

    private final UserQueryRepository userQueryRepository;

    public List<UserNicknameResult> findNicknamesByUserIds(Collection<Long> userIds) {
        return userQueryRepository.findNicknamesByUserIds(userIds);
    }
}
