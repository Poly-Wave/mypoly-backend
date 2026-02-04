package com.polywave.billservice.application.category.query.service;

import com.polywave.billservice.application.category.query.result.UserCategoryInterestResult;
import com.polywave.billservice.repository.query.UserBillInterestQueryRepository;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBillInterestQueryService {

    private final UserBillInterestQueryRepository userBillInterestQueryRepository;

    /**
     * 사용자 현재 관심 카테고리 ID 조회
     */
    public Set<Long> getCurrentCategoryIds(Long userId) {
        return userBillInterestQueryRepository.findCategoryIdsByUserId(userId).stream()
                .map(UserCategoryInterestResult::categoryId)
                .collect(Collectors.toSet());
    }
}