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
     * 온보딩 단계: 카테고리 설정 완료상태로 변경
     * 이미 완료된 사용자라면 예외 발생
     */
    @Transactional
    public void completeCategoryOnboarding(Long userId) {
        String currentStatus = userServiceClient.getOnboardingStatus(userId);
        if (!List.of("SIGNUP", "ONBOARDING").contains(currentStatus)) {
            throw new IllegalArgumentException("이미 온보딩 기능이 완료되었거나 접근할 수 없는 상태입니다.");
        }
        userServiceClient.updateOnboardingStatus(userId, ONBOARDING_STATUS_CATEGORY);
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