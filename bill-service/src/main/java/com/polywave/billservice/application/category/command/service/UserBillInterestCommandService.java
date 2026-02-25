package com.polywave.billservice.application.category.command.service;

import com.polywave.billservice.client.UserServiceClient;
import com.polywave.billservice.domain.BillCategory;
import com.polywave.billservice.domain.UserBillInterest;
import com.polywave.billservice.repository.command.CategoryCommandRepository;
import com.polywave.billservice.repository.command.UserBillInterestCommandRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserBillInterestCommandService {

    private static final String ONBOARDING_STATUS_CATEGORY = "CATEGORY";

    private final UserBillInterestCommandRepository userBillInterestCommandRepository;
    private final CategoryCommandRepository categoryCommandRepository;
    private final UserServiceClient userServiceClient;

    /**
     * 관심사 추가
     *
     * AppService에서 하나의 트랜잭션으로 묶어 처리하는 게 자연스럽기 때문에
     * 별도 전파(REQUIRES_NEW)는 두지 않습니다.
     */
    @Transactional
    public void addInterests(Long userId, Set<Long> categoryIds) {
        // user-service에 온보딩 상태 CATEGORY로 업데이트 요청 (관심사 추가할 항목이 없어도 진행 상태는 업데이트해야 함)
        userServiceClient.updateOnboardingStatus(userId, ONBOARDING_STATUS_CATEGORY);

        if (categoryIds.isEmpty())
            return;

        // 활성화된 카테고리만 추가
        List<BillCategory> categories = categoryCommandRepository.findAllByIdInAndIsActiveTrue(categoryIds);

        List<UserBillInterest> interests = categories.stream()
                .map(c -> UserBillInterest.of(userId, c))
                .toList();

        userBillInterestCommandRepository.saveAll(interests);
    }

    /**
     * 관심사 삭제
     */
    @Transactional
    public void removeInterests(Long userId, Set<Long> categoryIds) {
        if (categoryIds.isEmpty())
            return;
        userBillInterestCommandRepository.deleteByUserIdAndCategory_IdIn(userId, categoryIds);
    }
}