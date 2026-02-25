package com.polywave.billservice.application.category;

import com.polywave.billservice.application.category.command.service.CategoryInterestCommand;
import com.polywave.billservice.application.category.command.service.InterestDiff;
import com.polywave.billservice.application.category.command.service.UserBillInterestCommandService;
import com.polywave.billservice.application.category.query.service.UserBillInterestQueryService;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserBillInterestAppService {

    private final UserBillInterestQueryService queryService;
    private final UserBillInterestCommandService commandService;

    /**
     * 관심사 업데이트 전체 흐름
     * 전체 흐름 단위로 트랜잭션 보장
     */
    @Transactional
    public void updateInterests(CategoryInterestCommand command) {
        Long userId = command.userId();
        Set<Long> requested = new HashSet<>(command.categoryIds());

        // 1. 현재 관심사 조회 (QueryService)
        Set<Long> current = queryService.getCurrentCategoryIds(userId);

        // 2. 추가/삭제 차이 계산
        InterestDiff diff = InterestDiff.of(current, requested);

        // 3. CommandService 호출 (같은 트랜잭션에서 수행)
        commandService.addInterests(userId, diff.toAdd());
        commandService.removeInterests(userId, diff.toRemove());
    }

    /**
     * 온보딩 단계 전용 관심사 저장
     * 일반 업데이트와 동일하게 관심사를 저장하고, 추가로 온보딩 완료 상태로 변경합니다.
     */
    @Transactional
    public void saveOnboardingInterests(CategoryInterestCommand command) {
        updateInterests(command);
        commandService.completeCategoryOnboarding(command.userId());
    }
}