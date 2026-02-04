package com.polywave.billservice.application.category.command.service;

import com.polywave.billservice.application.category.query.result.UserCategoryInterestResult;
import com.polywave.billservice.domain.BillCategory;
import com.polywave.billservice.domain.UserBillInterest;
import com.polywave.billservice.repository.command.CategoryCommandRepository;
import com.polywave.billservice.repository.command.UserBillInterestCommandRepository;
import com.polywave.billservice.repository.query.UserBillInterestQueryRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryInterestCommandService {

    private final UserBillInterestQueryRepository userBillInterestQueryRepository;
    private final UserBillInterestCommandRepository userBillInterestCommandRepository;
    private final CategoryCommandRepository categoryCommandRepository;

    @Transactional
    public void updateInterests(CategoryInterestCommand command) {
        // 사용자 ID와 요청된 관심 카테고리 ID 목록 가져오기
        Long userId = command.userId();
        Set<Long> requested = new HashSet<>(command.categoryIds());

        // 현재 DB에 저장된 사용자의 관심 카테고리 ID 가져오기
        Set<Long> current = loadCurrentCategoryIds(userId);

        // 현재 관심사와 요청된 관심사 차이를 계산
        InterestDiff diff = InterestDiff.of(current, requested);

        // 새로 추가해야 할 관심사 DB에 저장
        addInterests(userId, diff.toAdd());

        // 삭제해야 할 관심사 DB에서 제거
        removeInterests(userId, diff.toRemove());
    }

    /**
     * 현재 사용자가 가지고 있는 관심 카테고리 ID를 DB에서 조회
     */
    private Set<Long> loadCurrentCategoryIds(Long userId) {
        return userBillInterestQueryRepository.findCategoryIdsByUserId(userId).stream()
                .map(UserCategoryInterestResult::categoryId)
                .collect(Collectors.toSet());
    }

    /**
     * 새로운 관심 카테고리 추가
     * @param userId 관심사를 추가할 사용자 ID
     * @param categoryIds 추가할 카테고리 ID 집합
     */
    private void addInterests(Long userId, Set<Long> categoryIds) {
        if (categoryIds.isEmpty()) return;

        // DB에서 활성화된 카테고리 정보 조회
        List<BillCategory> categories =
                categoryCommandRepository.findAllActiveByIdIn(categoryIds);

        // UserBillInterest 엔티티로 변환
        List<UserBillInterest> interests = categories.stream()
                .map(c -> UserBillInterest.of(userId, c))
                .toList();

        // DB에 저장
        userBillInterestCommandRepository.saveAll(interests);
    }

    /**
     * 기존 관심 카테고리 삭제
     * @param userId 관심사를 제거할 사용자 ID
     * @param categoryIds 제거할 카테고리 ID 집합
     */
    private void removeInterests(Long userId, Set<Long> categoryIds) {
        if (categoryIds.isEmpty()) return;

        // JPA query method를 사용하여 삭제
        userBillInterestCommandRepository.deleteByUserIdAndCategoryIdIn(userId, categoryIds);
    }
}
