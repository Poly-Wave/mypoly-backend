package com.polywave.billservice.application.category.command.service;

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

    private final UserBillInterestCommandRepository userBillInterestCommandRepository;
    private final CategoryCommandRepository categoryCommandRepository;

    /**
     * 관심사 추가
     *
     * AppService에서 하나의 트랜잭션으로 묶어 처리하는 게 자연스럽기 때문에
     * 별도 전파(REQUIRES_NEW)는 두지 않습니다.
     */
    @Transactional
    public void addInterests(Long userId, Set<Long> categoryIds) {
        if (categoryIds.isEmpty()) return;

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
        if (categoryIds.isEmpty()) return;
        userBillInterestCommandRepository.deleteByUserIdAndCategory_IdIn(userId, categoryIds);
    }
}