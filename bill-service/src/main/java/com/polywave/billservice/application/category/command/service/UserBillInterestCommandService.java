package com.polywave.billservice.application.category.command.service;

import com.polywave.billservice.domain.BillCategory;
import com.polywave.billservice.domain.UserBillInterest;
import com.polywave.billservice.repository.command.CategoryCommandRepository;
import com.polywave.billservice.repository.command.UserBillInterestCommandRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserBillInterestCommandService {

    private final UserBillInterestCommandRepository userBillInterestCommandRepository;
    private final CategoryCommandRepository categoryCommandRepository;

    /**
     * 관심사 추가
     * 단독 호출 가능하도록 트랜잭션 설정
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addInterests(Long userId, Set<Long> categoryIds) {
        if (categoryIds.isEmpty()) return;

        List<BillCategory> categories = categoryCommandRepository.findAllActiveByIdIn(categoryIds);

        List<UserBillInterest> interests = categories.stream()
                .map(c -> UserBillInterest.of(userId, c))
                .toList();

        userBillInterestCommandRepository.saveAll(interests);
    }

    /**
     * 관심사 삭제
     * 단독 호출 가능
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void removeInterests(Long userId, Set<Long> categoryIds) {
        if (categoryIds.isEmpty()) return;
        userBillInterestCommandRepository.deleteByUserIdAndCategoryIdIn(userId, categoryIds);
    }
}